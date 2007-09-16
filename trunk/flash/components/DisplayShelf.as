package components {

    import flash.events.Event;
    import flash.events.EventDispatcher;
    import flash.events.KeyboardEvent;
    import flash.events.MouseEvent;
    import flash.geom.Matrix;
    import flash.ui.Keyboard;
    import flash.utils.Dictionary;
    
    import mx.controls.Image;
    import mx.core.ClassFactory;
    import mx.core.IFactory;
    import mx.core.UIComponent;
    import mx.effects.AnimateProperty;
    import mx.effects.easing.Quadratic;
    import mx.events.CollectionEvent;
    import mx.managers.IFocusManagerComponent;
    import states.State;

    // defining styles on the DisplayShelf.  By defining these styles here in metadata, developers will be allowed
    // to specify values for these styles as attributes on the MXML tag.  Note that this component doesn't actually
    // use these styles...instead, the TiltingTiles it contains use them. But this component assigns _itself_ as the 
    // stylename for those TiltingTile instances. That makes the tiltingTile inherit all the style values defined on this component.
    // Thus by defining the styles on this component, we are automatically passing them through to the contained subcomponent.
    // this is a common practice for aggregating subcomponents.
    [Style(name="borderThickness", type="Number")]
    [Style(name="borderColor", type="Number")]    
    // defining the change event. This event is dispatched whenever the selectedIndex of this component changes. By declaring it 
    // here, in metadata, we allow developers to specify a change handler on our MXML tag.    
    [Event("change")]    
    
    // defining the default property.  By declaring dataProvider as our defaultProperty, we are allowing developers to specify the value of 
    // default property as the content of the DisplayShelf tag, without having to explciitly call it out as the value for defaultProperty.
    [DefaultProperty("dataProvider")]
	[DefaultProperty("numItems")]
	[DefaultProperty("gap")]
    
    public class DisplayShelf extends UIComponent implements IFocusManagerComponent {
    	
        // how far, in pixels, each child will be spaced when stacked sideways. 
        // This probably should be a percentage of the size of the children...i.e., overlap 1/5th...but
        // we're taking a shortcut here by defining it in pixels.        
        private var kPaneGap:Number = 100;

        //---------------------------------------------------------------------------------------
        // private state
        //---------------------------------------------------------------------------------------

        // how far our selected item should 'pop' in front of the non-selected items.  We'll use this value to compute a scale-down factor for
        // the non-selected items.
        private var _popout:Number = .2;

        // our array of children.  These are the TiltingTiles that we'll generate, one for each item in the dataProvider.
        private var _children:Array = [];



        
        // the tilt angle for the non-selected children. This can be set by the developer.
        private var _angle:Number = 5;
        // the current selected index, as set by the developer.
        private var _selectedIndex:Number = 0;        
        // the index (or rather, value, since it can be fractional) of the item at the center of our list as currently displayed. Since we animate from
        // selected index to selected index when it changes, our 'current' position is different from the 'selected' position. By keeping track of this 
        // value, we can make sure that when we draw we're always drawing the 'current' index as it animates towards the selected index.
        private var _currentPosition:Number = 0;
        
        
        // a map that allows us to use an itemRenderer (actually, a tiltingTile) as a key to map back to the index of the it represents. We'll use this when
        // the user clicks on one of the tilting tiles to decide what our new selected index is.  We _could_ just iterate over our children list to 
        // find the index on click, but there are lots of use cases where you need to store extra data about an itemRenderer that can't be easily looked up.
        // in those cases, Dictionaries are really useful tools. So we'll use one here just as a demonstration.
        private var _itemIndexMap:Dictionary = new Dictionary(true);


        // a flag to control whether we want to automatically enable history management when the selected index changes.  This way the component
        // can be used in scenarios where it doesn't represent a 'location' to the user.
        private var _enableHistory:Boolean = false;
        // these are structures we'll need temporarily when calculating layout. Rather than allocating them again and again on update, we'll just allocate them
        // once and hold on to them.
        private var lCP:ChildPosition = new ChildPosition();
        private var rCP:ChildPosition = new ChildPosition();
        // the selected index, clamped to the range defined by the dataProvider. We store this separate from the actual selected index as assigned by the developer.
        // we want to calculate it only once and then store it off. But if we stored it back into our selected index property, we'd need to worry about scenarios where
        // the selectedIndex gets assigned before the dataprovider does. So we store it in a separate variable, so as not to trample the 'true' selected index.
        private var _safeSelectedIndex:Number;

        // the effect we'll use to animate from old to new selected index.  If the user changes selected index in the middle of an animation, we'll want to cancel
        // the old one, so we keep a reference to it.
		private var _animation:AnimateProperty;
         
		// whether or not we should automatically select a child when the user clicks on a particular item.  It's generally good practice to avoid hard coding UI gestures
		// into your component if you can avoid it...if possible, a good component will provide a default UI gesture, a way to disable it, and a programmatic way to 
		// build an alternate UI gesture. In this case, by default we select an item on click, we allow the developer to turn that off, and we allow the developer to
		// set the selectedIndex programmatically so they can select on, say mouse over.         
		private var _selectOnClick:Boolean = true;
		
		
		private var nItems:int = 0;
		
		private var myItems:Array = new Array(); // items!

		
		// really, we should maintain an internal array of State objects, and readd them every time
		// as opposed to creating them each time, as we are doing now...

		
		public function addItem():void {
			var state:State = new State();
			state.pageHeight = height;


			var i:int = myItems.length; // the index we need for the tilting pane

            var tilt:TiltingPane = new TiltingPane();
            /*   put an entry in our dictionary mapping our tilting tile to its index in the dataProvider.
            *    When the user clicks on one of our tilting tile, we'll use this map to figure out the index
            *    of the item they just clicked on, and hence what our new selected index should be */
            _itemIndexMap[tilt] = i;

	        /*  add the tilting tile to our array of children*/
	        _children[i] = tilt;
            
            /*   add a click event handler to our tiltingPane, so we can automatically update the selected index.
            *    note that we're again using weak references here for our event listener.  In this case, since these are
            *    entirely self contained objects, we don't actually need to use a weak listener here.  But we're a bit lazy,
            *    and since we know that we're not going to run into any of the pitfalls of weak references, we go ahead
            *    and use them anyway. Alternatively, we could have been explicit about removing the listener when we 
            *    removed the tilting panes later on.
            */
            tilt.addEventListener(MouseEvent.CLICK,itemClickHandler,false,0,true);

            /*   set the tiltingTile's styleName to us, the parent componment. This is common practice for styling sub-components
            *    of a parent component.  By doing this, the TiltingTile inherits _all_ of our styles...not just the inheriting ones...
            *    which allows us to easily facade style values from the children up through us for our component developers to specify in CSS.
            */
            tilt.styleName = this;
            tilt.content = state;
            addChildAt(tilt,0);
            myItems.push(state);
            numItems = myItems.length;
			selectedIndex = numItems-1;
		}
		
		
		public function set gap(g:int):void {
            kPaneGap = g;
		}

        //---------------------------------------------------------------------------------------
        // constructor
        //---------------------------------------------------------------------------------------

        public function DisplayShelf() {
            super();
        }


        //---------------------------------------------------------------------------------------
        // public properties
        //---------------------------------------------------------------------------------------

        /*  True if the developer wants us to automatically save changes to the selectedIndex in the history manager or not.
        */
        public function set enableHistory(value:Boolean):void
        {
            _enableHistory = value;            
        }
        public function get enableHistory():Boolean
        {
            return _enableHistory;
        }
        

        /*  How far out the selected item should 'pop' from the background items.  A value of 0 doesn't pop it out at all, while a value of 1 will receed
        *    the background items infinitely to the horizon.   Basically, the value is inverted and used as a scale factor for the background items.
        *    pick something appropriate.
        *    FWIW, now that I look at this, it really should be a style, not a property
        */        
        [Bindable] public function set popout(value:Number):void
        {
            _popout = value;
            /*     Being a good flex component, we don't want to recalculate every time someone changes this value. Instead, we store the change,
            *     and invalidate so we'll get to redrew the next time the screen is going to be updated.
            */
            invalidateDisplayList();
        }
        public function get popout():Number
        {
            return _popout;
        }
        
        /*    the index of the currently selected item in the dataProvider.  Note that since this component animates its position, this is not necessarily the
        *    same as the item we are currently looking at. We might be in the middle of animating towards the selected item.
        *    note that since we are going to dispatch a well defined, named event when this value changes, we specify that 
        *    event in the binding metadata. That let's flex know that we're going to be reponsible for dispatching the event ourselves.
        *    Otherwise the binding metadata would result in _another_ event being dispatched, which would be wasteful.
        */
        [Bindable("change")]
        public function set selectedIndex(value:Number):void
        {
            // save time and performancing by doing nothing if the selected index is already the new value.
            if(_selectedIndex == value)
                return;
                
            // store off the new value.
            _selectedIndex = value;
            
            // since we are going to use this value to index into the item renderers, we want to make sure we don't use a value outside the range of
            // existing renderers. Rather than having to litter our code with those checks all over the place, we'll clamp it to the legal range once now 
            // and store off the 'safe' value.
            _safeSelectedIndex = Math.max(0,Math.min(_selectedIndex,_children.length-1));

            // dispatch an event letting listeners know that 
            dispatchEvent(new Event("change"));            
            // when the selected index changes, we'll want to kick-start our animation.
            startAnimation();
        }
        
        public function get selectedIndex():Number
        {
            return _selectedIndex;
        }


        /*    This property represents the current position in the child items that our component is looking at.
        *    All of our rendering is done off of this property. By exposing it as a public property, we can animate
        *    it, which means that even though we're incorporating animation, our rendering will always be in sync
        *    with the internal state of our application
        */
        public function set currentPosition(value:Number):void
        {
            _currentPosition = value;
            invalidateDisplayList();
        }
        public function get currentPosition():Number
        {
            return _currentPosition;
        }

		public function refresh():void {
            invalidateDisplayList();
            invalidateProperties();
            invalidateSize();
		}

		
        
        [Bindable]
        public function set numItems(n:int):void {
        	nItems = n;
        	refresh();
        }
        
        public function get numItems():int {
            return nItems;
        }
        
        /* The angle of the background non-selected items*/
        public function set angle(value:Number):void {        
            _angle = value;
            invalidateDisplayList();
        }
        
        public function get angle():Number {
            return _angle;
        }
        
         /*    whether or not we should automatically select a child when the user clicks on a particular item.  It's generally good practice to avoid hard coding UI gestures
         *    into your component if you can avoid it...if possible, a good component will provide a default UI gesture, a way to disable it, and a programmatic way to 
         *    build an alternate UI gesture. In this case, by default we select an item on click, we allow the developer to turn that off, and we allow the developer to
         *    set the selectedIndex programmatically so they can select on, say mouse over.         
        */
        public function set selectOnClick(value:Boolean):void
        {
            _selectOnClick = value;        
        }
        public function get selectOnClick():Boolean
        {
            return _selectOnClick;
        }
        
        //---------------------------------------------------------------------------------------
        // property management
        //---------------------------------------------------------------------------------------

        /*   this is the standard function where components put performance intensive computations and side-effects
        *    from changes to their properties.  By calling invalidateProperties(), a component guarantees that this function
        *    will get called by the layout manager before the next time the screen is going to be updated, before their 
        *    measure() or updateDisplayList() functions are called (if necessary).  Note that there is no guarantee about the
        *    order in which commitProperties is called from component to component (i.e., it's not parent before child or vice versa).
        */
        override protected function commitProperties():void {
            /*     since the size of our dataProvider might have just changed, we'll revalidate our selected index to make sure it's 
            *    a valid index into the data.
            */
            _safeSelectedIndex = Math.max(0,Math.min(_selectedIndex,_children.length-1));
            
            /*    since we've just recalculated our state, chances are pretty good we need to re-render ourselves now.
            */
            invalidateDisplayList();
        }

        //---------------------------------------------------------------------------------------
        // measurement 
        //---------------------------------------------------------------------------------------
        
        /*    this is our measurement function.  A component's measure() routine is where it should calculate what it's 'natural' size should be...
        *    i.e., how big it would like to be if the developer doesn't assign it an explicit size. The layout manager calls this function whenever
        *    it thinks your component needs to remeasure itself. That happens under a number of circumstances. a) some state that your measured size
        *    uses in calculation changes, so your component explicitly calls invalidateSize() (i.e., see the set angle() function).  b) the measured or explicit 
        *    of one of your children changes size...the layout manager assumes that your measured size relies on the size of your children, so it will ask you to
        *    re-measure.  Note that according to the conventions of the SDK, your measured size is generally ignored if you have an explicit size set. That means
        *    that the layout manager might optimize by not calling your measure() routine if you have an explicit size. So don't do any calculation in here that
        *    _must_ happen for your component to function properly.
        */
        override protected function measure():void
        {
            var mHeight:Number = 0;
            var mWidth:Number = 0;
            var t:TiltingPane;
        
            /*     Since each child could potentially be at the middle of the component at its natural size with the other children stacked along side it, we need to
            *    look at each child to calculate our measured size.
            *    So for each child:
            */
            for(var i:int = 0;i<_children.length;i++)
            {
                t = _children[i];
                /*     our measured size will just be the largest measured size of all of our children, to make 
                *    sure we can correctly render all of them*/                
                mHeight = Math.max(t.measuredHeight,mHeight);
                /*     our measured width, however, is more complicated. For each child, we want to calculate how big we need to be if that child was selected.
                *    That's the size of the child plus the amount of space we need to stack the other children in the background.  Now since each child has a different
                *    number of children to the left and right, that will be different for each child.  But we want the child to stay in the middle, so we need it to be
                *    symmetrical. So our calculation is:  figure out how many children go to the left and right of child N.  Take the maximum of that, figure out how 
                *    much space we need to stack those children in the background, and double that, since we want the same amount of space on each side.  Now add the measured
                *    width of child N, since it's going to be at the middle. That's how much space we need for Child N.  Now for all children, calculate that, and take the 
                *    largest number we find
                */
                mWidth = Math.max(mWidth, t.measuredWidth  + Math.max(i,_children.length - i - 1) * kPaneGap * 2);
            }
            /*    store off our measured sizes. If we were really being good, we'd probably calculate a minimum size too. But since our component doesn't adjust its layout            
            *    to match its size, we don't really have a minimum size. i.e., if we wanted to we could squeeze the overlapping children in the background together if we 
            *    didn't have enough space.  If that were the case, our minimum size would be the same calculation above, but for whatever we consider our 'minimum' overlap
            *    to be.  I'll leave that as an exercise for the reader 
            */
            measuredHeight = mHeight;
            measuredWidth = mWidth;
        }
        
        //---------------------------------------------------------------------------------------
        // layout
        //---------------------------------------------------------------------------------------

        /*    this is our main function that does all of our layout and rendering.  The LayoutManager takes care of making sure this function gets called
        *    right before the screen is updated if our component needs updating. We tell the layout manager that we need updating by calling 
        *    invalidateDisplayList(). This automatically happens whenever our component changes size.
        */        
        override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
        {
            /*  often list-like components need to do things like check values of the first child as a starting point for calculations.
            *    that assumes we have a first child, and can RTE if we're empty. Rather than putting guards all over the place, we'll just
            *    bail out here if we have no children at all
            */
            if(_children.length == 0)
                return;

            /*    ChildPosition is a simple value structure we use to pass around the calculated position and angle of a single child.  It's defined at 
            *    the end of this file.
            */
            var c:ChildPosition = new ChildPosition();
            var t:TiltingPane;
            var m:Matrix;
            
            /* for each child to the left of our currently selected child...
            */
            for(var i:int = 0;i<=_currentPosition;i++)
            {
                /* calculate where it should be based on our currently focused position. This function is defined below.
                */
                calcPositionForSelection(i,_currentPosition,c);                
                t = _children[i];
                /*  and put it there. Note that the first thing we do is set the size of the child to its requested (measured or explicit size).
                *    in Flex, it's always that parent's responsibility to tell a child what size it should be. If we didn't do this, the child 
                *    would just sit at size 0, even if we or someone else explciitly set the width/height properties.   
                *    In this case, we're not doing any resizing of the children.  So we just want them to be their measured size, or their explicit
                *    size if someone has set an explicit size on them.  This is such a common if/or calculation that UIComponents have a convenience function
                *    defined called 'getExplciitOrMeasuredWidth/Height().'
                *    Note that we use the setActualSize function.  Components sizing their children should _always_ use this function.  It serves two purposes:
                *    first, it differentiates between the _explicit_ size that might be set by the developer, and the 'current actual' size that the parent wants
                *    the child to be.  Since explicit size probably is used in a parent's computed measurement and layout algorithms, we don't want to confuse
                *    the inputs to those algorithms with the output (the actual size). Second, since explicit size of a child is usually an input to the parent's
                *    measurement and layout, any time they change the parent needs to be invalidated and re-layout. So if we accidentally set the explicit size here,
                *    it would trigger another invalidation and layout, and potentially an infinite layout loop.  Instead, setting the actual size doesn't invalidate
                *    the parent (since it assumes child actual size is _not_ an input to the parent's measurement/layout algorithm).
                */
                t.setActualSize(t.getExplicitOrMeasuredWidth(),t.getExplicitOrMeasuredHeight());
                /*     we need to set the angle of the child here.  Now TiltingTiles use their angle as an input to their measured size. And as with all UICompoennts,
                *    their measured size is an input to their parent's measurement and layout algorithms. So if we weren't careful, setting their angle here would
                *    force another measurement/layout on our component, causing an infinite loop. So as with size, we need to differentiate between the explicit
                *    angle of the TiltingTile, and the 'acutal' angle as assigned by the parent (us).
                */
                t.setActualAngle(c.angle);
                /*  we want the children on the left to stack up from left to right. Children with higher indexes go above children with lower indexes, so we'll
                *    set the child index here to get it stacking correctly 
                */
                setChildIndex(t,i);
                /*  Lastly, we'll set the scale of the tilting tile. Selected children are displayed at full scale, while background children are scaled down a bit
                *    to make it look like they're receding into the distance.  Now scale is _also_ an input into the measured size of a child (set its scale to 2X, 
                *    and its measured size doubles) so we have the same problem with scale as we do with sizing and angle above.  Flex unfortunately doesn't differentiate
                *    between explicit and actual scale. But we can work around it by manipulating the child component's scale factors in its matrix, which shortcuts                
                *    the part of the framework that causes the invalidate and potentially the infinite loop. This is a hack, one we hopefully won't need in an upcoming release
                *    of the SDK.
                */
                m = t.transform.matrix;
                /*     assign the scale */
                m.a = m.d = c.scale;
                /*     and assign the matrix back to the item.  Matrices are copy on access...meaning when you ask for the matrix of an object, you get a copy of it.
                *    So our changes won't affect the object until we assign it back to the child as its transform matrix 
                */
                t.transform.matrix = m;
                /*    set its location */
                t.move(c.x,c.y);
            }

            /*     this is exactly the same logic as the previous loop, except that we want to stack each child on the right hide side _below_ the previous child.
            */
            for(i = Math.floor(_currentPosition)+1; i< _children.length;i++)
            {
                calcPositionForSelection(i,_currentPosition,c);                
                t = _children[i];
                t.setActualSize(t.getExplicitOrMeasuredWidth(),t.getExplicitOrMeasuredHeight());
                t.setActualAngle(c.angle);
                t.move(c.x,c.y);
                /*     each time we move to the next child, we set its index to 0. This bumps all previous children up one level, and puts this child at the bottom,
                *      ensuring it ends up below the child to its left
                */
                setChildIndex(t,0);
                m = t.transform.matrix;
                m.a = m.d = c.scale;
                t.transform.matrix = m;
            }
            
            /* lastly, we make sure the currently selected child is on top
            */
            setChildIndex(_children[Math.round(_currentPosition)],numChildren-1);
        }
        
        /*  this function calculates the scale, angle, and position a child should be given a particular 
        *    selected position.  Since we animated our 'currentPosition', we need to be able to calculate        
        *    selected position for any real positive number. To do that, we calculate two different positions
        *    and average them out. If, for example, the current position was 3.7, we'll calculate the values
        *    for a currentPosition of 3, and a currentPosition of 4, and average .7 of the first and .3 of the second
        */
        private function calcPositionForSelection(i:Number,sel:Number,c:ChildPosition):void
        {
            var delta:Number = sel - Math.floor(sel);
            /* if sel is already an integer, we just calculate our position for that integer, and return
            */
            if(delta == 0)
            {
                calcPositionForIndexSelection(i,sel,c);
                return;
            }
            /* otherwise, calculate our position for the previous and next integers
            */
            calcPositionForIndexSelection(i,sel-delta,lCP);
            calcPositionForIndexSelection(i,sel-delta+1,rCP);
            /* and compute a weighted average
            */
            c.angle = lCP.angle + delta * (rCP.angle - lCP.angle);
            c.scale = lCP.scale + delta * (rCP.scale - lCP.scale);
            c.x = lCP.x + delta * (rCP.x - lCP.x);
            c.y = lCP.y + delta * (rCP.y - lCP.y);
        }

        /*    this function calculates the position for a given child assuming our currentPosition value is 'sel.'
        *    unlike the previous funciton, this one assumes that sel is an integer.
        */
        private function calcPositionForIndexSelection(i:Number,sel:Number,c:ChildPosition):void
        {            
            var t:TiltingPane = _children[i];
            var selected:TiltingPane = _children[sel];
            var adjacent:TiltingPane;
            var a:Number = _angle;

            if(i == sel)
            {
                /*    if the item we're calculating the position for _is_ the selected item,
                *    then we know exactly where it goes...smack dab in the middle, at full size, full scale,
                *    with an angle of 0.
                */
                c.scale = 1;
    
                c.x = unscaledWidth/2 - t.getExplicitOrMeasuredWidth()/2;
                c.y = unscaledHeight/2 - t.getExplicitOrMeasuredHeight()/2;
                c.angle = 0;
            }
            else if (i < sel)
            {
                /*     otherwise, if it's to the left of the selected item,
                *    we want to scale it down to make it look like it's receding into the background...
                */
                c.scale = (1-_popout);
                /* tilt it in towards the selected item */
                c.angle = _angle;
                /*  and push it off to the left.  To do that, we need to calculate it's position. Most of the children to the left just go a fixed distance
                *    from the child to its right, and so we don't care about their actual size. But the first child immediately to the left of the selected child
                *    is mostly visible, so we need to position it so that only a little bit overlaps. Which means we need to know it's size.  
                *    so first, let's calculate the position of that first child to the left.
                *    that's going to be the left edge of the selected item, minus approximately 8/10th of the widths of the next item over (since we want it to
                *    overlap by about 2/10ths.)
                */
                adjacent= _children[sel-1];
                var leftBase:Number = unscaledWidth/2 - selected.widthForAngle(0)/2 - (adjacent.getExplicitOrMeasuredWidth()/2 +adjacent.widthForAngle(a)*2/10) * c.scale;
                /*    now that we know where that first item to the left sits, we can calculate the position of our child as a simple fixed distance based on how many
                *    children sit between it and that first item to the left
                */
                c.x = leftBase - kPaneGap*(sel-1-i),
                /*    lastly, center it vertically */
                c.y = unscaledHeight/2 - t.getExplicitOrMeasuredHeight()* (1-_popout)/2;
            }
            else
            {
                /*     this is basically the same logic as above, but for children to the right of the selection.  It sets it to 
                *    a negative angle, and calculates the position as a distance from the first child to the right of the selection.
                */
                c.scale = (1-_popout);
                adjacent = _children[sel+1];
                var rightBase:Number  =  unscaledWidth/2 + selected.widthForAngle(0)/2 + (adjacent.widthForAngle(-_angle)*3/10 - adjacent.getExplicitOrMeasuredWidth()/2) * c.scale;
                c.angle = -_angle;
                c.x = rightBase + kPaneGap*(i-(sel+1));
                c.y = unscaledHeight/2 - t.getExplicitOrMeasuredHeight() * (1-_popout)/2;
            }
        }
        
    
        //---------------------------------------------------------------------------------------
        // interaction
        //---------------------------------------------------------------------------------------

        /*    this is our event handler for when a user clicks on an item.
        */
        private function itemClickHandler(e:MouseEvent):void
        {
            /* again, if the developer wants different UI behavior, allow them to disable this */
            if(_selectOnClick == false)
                return;
                
            /*     find out what the index is of the selected item.  To do this, we map back from the 
            *      item clicked on to an index in our itemIndexMap.  Since we re-order our children
            *      to get depth and layering correct, we couldn't necessarily just ask for the child index...
            *    the child index would be different from the item's index in the dataProvider.  We could
            *    iterate over the children array to find the one that was clicked on, but that might have
            *    bad performance implications. Instead, we use a dictionary to quickly map from a child to
            *    an index.  This is a really useful way to generally store metadata about your items/renderers
            *    in custom components.
            */
            var index:Number = _itemIndexMap[e.currentTarget];
            selectedIndex = index;
        }
        
        /*     this is our event handler for when our dataProvider changes.
        *    in this case, all we do is set a flag indicating that we want to regenerate our item renderers,
        *    and invalidate our properties.  The change event from the collection typically carries additional
        *    data...was an item added, removed, or just changed?  We could, and really should, optimize how 
        *    we respond to this event based on what really happened...i.e., if an item was added, there's no
        *    need to regenerate _all_ our item renderers. Exercise for the reader ;)
        */
        private function dataChangeHandler(event:CollectionEvent):void {
            invalidateProperties();
        }

        //---------------------------------------------------------------------------------------
        // Keyboard Management
        //---------------------------------------------------------------------------------------

        /*    this event handler is where we respond to key presses when we have focus. Note that this event handler
        *    is already defined by the UIComponent base class...so we didn't have to add it anywhere. Instead, by 
        *    simply implementing the marker interface IFocusManagerComponent, and overriding this method, we get to
        *    handler key down events.
        */
        override protected function keyDownHandler(event:KeyboardEvent):void
        {
            super.keyDownHandler(event);
            switch(event.keyCode)
            {
                case Keyboard.LEFT:
                    selectedIndex = Math.max(0,selectedIndex-1);
                    event.stopPropagation();
                    break;
                case Keyboard.RIGHT:
                    selectedIndex = Math.min(numItems-1,selectedIndex+1);
                    event.stopPropagation();
                    break;                    
            }
        }

        //---------------------------------------------------------------------------------------
        // animation
        //---------------------------------------------------------------------------------------
        
        /* This is where we do our animation.  This function is called whenever the selected index changes.
        */
        private function startAnimation():void
        {
            /*     when you add animation to a component, you need to decide what will happen if two animations 
            *    try to fire at once. What happens, in this case, if the user sets the selected index while we're 
            *    still animating towards a previous selected index?
            *    Our decision here is to finish the previous animation (i.e., jump directly to the end of the animation).
            */
            if(_animation != null && _animation.isPlaying)
            {
                _animation.end();
            }
                
            /*     our animation is simple. Since our component tracks 'selectedIndex' and 'currentPosition' as separate concepts,
            *    animating is just a question of tweening the currentPosition variable to the selectedIndex value.
            *    every time the animation updates currentPosition, our component will invalidate and redraw. Easy as pie.
            */
            _animation = new AnimateProperty(this);
            _animation.property = "currentPosition";
            _animation.toValue = _selectedIndex;
            _animation.target = this;
            /*     if we picked a fixed duration, we'd have to deal with the fact that sometimes we're only moving a single position,
            *    and sometimes we may be moving a thousand.  Either short distances would be way too slow, or long distances would go 
            *    way to fast and look bad.  Instead, we'll calculate a duration based on how far we're animating.  We also put in a minimum animation
            *    so short distances don't go too quickly. We probably should also putting a cap so even in large data sets animations don't take too long.
            *    We could tweak this endlessly.
            */
            _animation.duration = Math.max(400,Math.abs(_selectedIndex - _currentPosition) * 200);
            _animation.easingFunction = mx.effects.easing.Quadratic.easeOut;
            _animation.play();
        }


        //---------------------------------------------------------------------------------------
        // history managmeent
        //---------------------------------------------------------------------------------------
        
        /*     These are the two methods a component needs to implement in order to save state with the history manager.
        *    in our constructor, we registered ourselves as a history enabled component with the history manager. Once that 
        *    happens, any time someone tries to save a state in the history, the manager will call this function to let our 
        *    component store off whatever values it needs to capture its current state
        */
        public function saveState():Object
        {
            if(_enableHistory == false)
                return {};
            /* all we really need to store is our selected index. */
            var index:int = _safeSelectedIndex == -1 ? 0 : _safeSelectedIndex;
            return { selectedIndex: index };
        }

        /*    this function, in turn, gets called whenever someone tries to navigate (back or forth) to a stored state.
        *    this funciton gives us a chance to read out our stored state and react accordingly.
        */        
        public function loadState(state:Object):void
        {
            if(_enableHistory == false)
                return;
        
            var newIndex:int = state ? int(state.selectedIndex) : 0;
            if (newIndex == -1)
                newIndex = 0;
            if (newIndex != _safeSelectedIndex)
            {
                // When loading a new state, we don't want to
                // save our current state in the history stack.
                var eh:Boolean = _enableHistory;
                _enableHistory = false;
                selectedIndex = newIndex;
                _enableHistory = eh;
            }
        }
    }
}
    

/* this little doodad is just a value object we use to store the position information for a single child.
*    by defining the class here, we don't clutter up the global namespace. This class is only visible inside this file.
*/
class ChildPosition {
    public var angle:Number;
    public var x:Number;
    public var y:Number;
    public var scale:Number;
}