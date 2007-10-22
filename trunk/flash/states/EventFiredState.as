package states
{
	import flash.display.Graphics;
	import flash.display.Sprite;
	
	import mx.core.UIComponent;
	import mx.states.SetEventHandler;
	import tools.ImageDisplay;
	import tools.Images;
	import mx.controls.Image;

	// An Event Fired
	// Visualize the Sheet and highlight in blue the event that just fired
	public class EventFiredState extends State {

		[Embed(source="../images/Paper.png")]
		public static const PaperImage:Class;

		private var shape:Sprite = new Sprite();
		private var pageHeightVal:int = 200;

		private const pageWidthInches:Number =8.5;
		private const pageHeightInches:Number = 11;
		
		private const aspectRatio:Number = pageWidthInches/pageHeightInches; // width / height
		
		private var paper:Image = new Image;
		
		
		
		public function EventFiredState():void {
			pageHeight = pageHeightVal;
			//addChild(shape);
			addChild(paper);
		}
		
		public function set pageHeight(h:int):void {
			pageHeightVal = h;
			explicitHeight = pageHeightVal;
			explicitWidth = explicitHeight * aspectRatio;

			draw();
		}

		// in inches....!
		// do some math to place it correctly
		public function addRegion(rx:Number, ry:Number, rw:Number, rh:Number):void {
			var reg:Sprite = new Sprite();
			var g:Graphics = reg.graphics;

			var rsx:Number = rx / pageWidthInches * explicitWidth;
			var rsy:Number = ry / pageHeightInches * explicitHeight;
			var rsw:Number = rw / pageWidthInches * explicitWidth;
			var rsh:Number = rh / pageHeightInches * explicitHeight;

			g.clear();
			g.lineStyle(1, 0x444444, 0.6);
			g.beginFill(0xCCCCCC, 0.15);
			g.drawRect(rsx, rsy, rsw, rsh);
			g.endFill();
			addChild(reg);
		}

		public function addCurrentlyActiveRegion(rx:Number, ry:Number, rw:Number, rh:Number):void {
			var reg:Sprite = new Sprite();
			var g:Graphics = reg.graphics;

			var rsx:Number = rx / pageWidthInches * explicitWidth;
			var rsy:Number = ry / pageHeightInches * explicitHeight;
			var rsw:Number = rw / pageWidthInches * explicitWidth;
			var rsh:Number = rh / pageHeightInches * explicitHeight;

			g.clear();
			g.lineStyle(2, 0x444444, 0.8);
			g.beginFill(0xFDFDFFF, 0.45);
			g.drawRect(rsx, rsy, rsw, rsh);
			g.endFill();
			addChild(reg);
		}
		
		override public function set displayShelfHeight(h:int):void {
			pageHeight = h;
		}
		
		private function draw():void {
			var g:Graphics = shape.graphics;
			g.clear();
			g.beginFill(0xFFFFFF, 0.99);
			g.drawRect(0, 0, explicitWidth, explicitHeight);
			g.endFill();

			paper.source = PaperImage;
			paper.width = explicitWidth;
			paper.height = explicitHeight;
		}
	}
}