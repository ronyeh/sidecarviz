package states
{
	import flash.display.Graphics;
	import flash.display.Sprite;
	
	import mx.core.UIComponent;
	import mx.states.SetEventHandler;

	public class VisualFeedback extends State {
		private var shape:Sprite = new Sprite();
		private var pageHeightVal:int = 200;
		private const aspectRatio:Number = 16/9; // width / height
		 
		public function VisualFeedback():void {
			pageHeight = pageHeightVal;
			addChild(shape);
		}
		
		public function set pageHeight(h:int):void {
			pageHeightVal = h;
			explicitHeight = pageHeightVal;
			explicitWidth = explicitHeight * aspectRatio;
			draw();
		}

		override public function set displayShelfHeight(h:int):void {
			pageHeight = 2/3*h;
		}
		
		private function draw():void {
			var g:Graphics = shape.graphics;
			g.clear();
			g.beginFill(0xFFFFFF, 0.99);
			g.drawRect(0, 0, explicitWidth, explicitHeight);
			g.endFill();
		}
	}
}