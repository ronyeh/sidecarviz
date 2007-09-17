package states
{
	import flash.display.Graphics;
	import flash.display.Sprite;
	
	import mx.core.UIComponent;
	import mx.states.SetEventHandler;
	import ink.InkStroke;
	import ink.Ink;
	import flash.geom.Rectangle;

	public class Page extends State {
		private var shape:Sprite = new Sprite();
		private var pageHeightVal:int = 200;

		private var inkWell:Ink = new Ink(false);
		 
		public function Page():void {
			pageHeight = pageHeightVal;
			addChild(shape);
			addChild(inkWell);
		}
		
		public function set pageHeight(h:int):void {
			pageHeightVal = h;
			explicitHeight = pageHeightVal;
			explicitWidth = explicitHeight*8.5/11;
			draw();
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
		}
		
		// clone this stroke onto our page...
		// do nothing to the stroke we passed in...
		public function addInkStroke(stroke:InkStroke):void {
			var xArr:Array = stroke.getXSamples();
			var yArr:Array = stroke.getYSamples();
			var newStroke:InkStroke = new InkStroke();
			newStroke.inkColor = 0x222266;
			for (var i:int=0; i<xArr.length; i++) {
				newStroke.addPoint(xArr[i], yArr[i]);
			}
			newStroke.rerenderWithCurves();
			inkWell.addStroke(newStroke);
			inkWell.recenterTheCenter(new Rectangle(0,0,explicitWidth,explicitHeight));
		}
	}
}