package states
{
	import flash.display.Graphics;
	import flash.display.Sprite;
	
	import mx.core.UIComponent;
	import mx.states.SetEventHandler;

	public class Page extends State {
		private var shape:Sprite = new Sprite();
		private var pageHeightVal:int = 200;
		 
		public function Page():void {
			pageHeight = pageHeightVal;
			addChild(shape);
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
	}
}