package tools {
	
	import flash.display.DisplayObject;
	import flash.display.LoaderInfo;
	import flash.events.Event;
	import flash.events.ProgressEvent;
	import mx.core.mx_internal;
	import mx.controls.Image;
	import mx.controls.Text;
	import mx.controls.Label;
	import flash.display.Sprite;
	
	
	// argh... why is mx_internal.as (sometimes) not accessible?
	// OH! Make sure you import mx.core.mx_internal;
	// it is removed sometimes (if you auto organize imports)
	//
	// Not sure if this is related, but how does the "Native Method" error pop up? (Error #1079)
	// 
	// TODO: Number is a 64-bit floating point number... so we should use int and uint whenever possible
	use namespace mx_internal; 

	// can't set smoothing, as it throws a cross-domain error... =\
	public class ImageDisplay extends Image {

		// the child of the above mx.control, used to get the width and height...
		// without throwing a cross-domain exception
		private var imageContent:DisplayObject; 
		private var imageURL:Object;
		private var loaded:Boolean = false;

		// for keeping track of the loading...		
		private var progressEventHandlerFcn:Function;

		// to keep track of loading progress
		private var numBytesLoaded:uint = 0;
	
		private var uniqueID:int = 0;
		
		// for debugging ImageDisplays
		private static var uniqueIDs:int = 1; // start from 1!
		private static var initializingSpacer:Boolean = false;
		
		// hide this later
		private var debugGraphic:Sprite = new Sprite();


		// constructor
		public function ImageDisplay():void {
			super();
			
			// createSpacer();

			x = 0;
			y = 0;
			
			addEventListener(Event.COMPLETE, loadedHandler);
			addEventListener(ProgressEvent.PROGRESS, progressEventHandler);
			
			uniqueID = uniqueIDs;
			uniqueIDs++;
		}

		public function get uniqueImageID():int {
			return uniqueID;
		}
		
		override mx_internal function contentLoaderInfo_completeEventHandler(event:Event):void {
			var loaderInfo:LoaderInfo = event.target as LoaderInfo;
			try {
				imageContent = loaderInfo.loader;
			} catch(err:SecurityError) {
				trace("Security Error Loading: " + loaderInfo.url);
			}
			super.contentLoaderInfo_completeEventHandler(event);
		}
		
		override public function load(url:Object = null):void {
			// do I have a parent already? Should probably remove myself from my parent
			
			visible = false;
			loaded = false;
			imageURL = url;
			super.load(url);
		}
		
		private function loadedHandler(e:Event):void {
			loaded = true;
			visible = true; // hidden until we have loaded successfully
			
			// displayDebugGraphic();
		}

		private function displayDebugGraphic():void {
			debugGraphic.graphics.clear();
			debugGraphic.graphics.lineStyle(2, 0xFFFFFF);
			for (var i:int=0; i<uniqueID; i++) {
				debugGraphic.graphics.drawCircle(50+((i%5) *13),80+i*7,10);
			}
			addChildAt(debugGraphic, 1); // on the top
		}

		// evict this image from our cache, and free resources
		// do not use it after the call to unload!
		public function unload():void {
			x = 0;
			y = 0;
			imageContent = null;
			imageURL = null;
			loaded = false;
			progressEventHandlerFcn = null;
			numBytesLoaded = 0;
			visible = false;
			
			// load the spacer!
			load("http://lite.piclens.com/images/spacer.gif"); // hopefully this removes the image buffer from the superclass?
			
			// am I still a child of a component?
			if (parent != null) {
				parent.removeChild(this); // free up references to this object!
			}
		}


		private function progressEventHandler(e:ProgressEvent):void {
			numBytesLoaded = e.bytesLoaded;

			// trace("ImageDisplay: Progress on " + imageURL);
			if (progressEventHandlerFcn==null) return;
			progressEventHandlerFcn(e);
		}

		public function set progressEventCallback(callback:Function):void {
			progressEventHandlerFcn = callback;
		}

		// the url we loaded from....
		public function get url():String {
			if (imageURL == null) { 
				return ""; 
			} else {
				return imageURL.toString();
			}
		}
		
		public function get isLoaded():Boolean {
			return loaded;
		}
		

		
		public function get imageContentWidth():Number {
			if (imageContent == null) {
				trace("ImageDisplay: Image Not Loaded! Returning default width..."); // should never get here!
				return 1024;
			}
			return imageContent.width;
		}
		
		public function get imageContentHeight():Number {
			if (imageContent == null) {
				trace("ImageDisplay: Image Not Loaded! Returning default height..."); // should never get here!
				return 768;
			}
			return imageContent.height;
		}

	}
}