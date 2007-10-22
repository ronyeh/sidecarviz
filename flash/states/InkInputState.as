package states
{
	import flash.display.Graphics;
	import flash.display.Sprite;
	
	import mx.core.UIComponent;
	import mx.states.SetEventHandler;
	import ink.InkStroke;
	import ink.Ink;
	import flash.geom.Rectangle;

	// This should be either input ink, or a visualization of the event handler firing...
	// Maybe it should contain both the ink and the event?
	public class InkInputState extends State {
		private var shape:Sprite = new Sprite();
		private var pageHeightVal:int = 200;

		private var inkWell:Ink = new Ink(false);

		private const aspectRatio:Number = 11/11; // width / height
		 
		public function InkInputState():void {
			pageHeight = pageHeightVal;
			addChild(shape);
			addChild(inkWell);
		}
		
		public function set pageHeight(h:int):void {
			pageHeightVal = h;
			explicitHeight = pageHeightVal;
			explicitWidth = explicitHeight*aspectRatio;
			draw();
		}

		override public function set displayShelfHeight(h:int):void {
			pageHeight = .8 * h;
		}
		
		private function draw():void {
			var g:Graphics = shape.graphics;
			g.clear();
			g.lineStyle(2, 0xCCCCCC, 0.9);
			g.beginFill(0x555555, 0.97);
			g.drawRect(0, 0, explicitWidth, explicitHeight);
			g.endFill();
		}
		
		// clone this stroke onto our page...
		// do nothing to the stroke we passed in...
		public function addInkStroke(stroke:InkStroke):void {
			var xArr:Array = stroke.getXSamples();
			var yArr:Array = stroke.getYSamples();
			var newStroke:InkStroke = new InkStroke();
			newStroke.inkColor = 0xCDCDDD;
			for (var i:int=0; i<xArr.length; i++) {
				newStroke.addPoint(xArr[i], yArr[i]);
			}
			newStroke.rerenderWithCurves();
			inkWell.addStroke(newStroke);
			inkWell.recenterTheCenter(new Rectangle(0,0,explicitWidth,explicitHeight));
		}
	}
}