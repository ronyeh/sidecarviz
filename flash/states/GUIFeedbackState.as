package states
{
	import flash.display.Graphics;
	import flash.display.Sprite;
	
	import mx.core.UIComponent;
	import mx.states.SetEventHandler;
	import mx.controls.Image;
	import tools.ImageDisplay;
	import flash.events.Event;

	// This should be a bitmap of the Swing component that was just displayed...
	// This represents GUI Feedback...
	public class GUIFeedbackState extends State {
		private var frame:ImageDisplay = new ImageDisplay();
		private var pageHeightVal:int = 200;
		 
		public function GUIFeedbackState():void {
			pageHeight = pageHeightVal;
			addChild(frame);
		}
		
		public function set pageHeight(h:int):void {
			pageHeightVal = h;
			draw();
		}

		override public function set displayShelfHeight(h:int):void {
			pageHeight = 2/3*h;
		}
		
		private function draw():void {
			frame.load("file:///C:/Documents%20and%20Settings/Ron%20Yeh/My%20Documents/Projects/SideCarViz/guifeedback/GUIFeedback.png");
			// frame.load("http://farm2.static.flickr.com/1019/869338020_1574a10ec8.jpg");
			frame.addEventListener(Event.COMPLETE, loaded);

		}
		
		private function loaded(e:Event):void {
			explicitHeight = pageHeightVal;
			explicitWidth = explicitHeight * frame.imageContentWidth / frame.imageContentHeight;
			frame.width = explicitWidth;
			frame.height = explicitHeight;
		}
	}
}