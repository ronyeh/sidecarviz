package components
{
	import flash.display.Graphics;
	import flash.display.Sprite;
	
	import mx.core.UIComponent;
	import mx.states.SetEventHandler;
	
	public class State extends UIComponent {
		
		private var shape:Sprite = new Sprite();
		 
		public function State():void {
			explicitWidth = 300;
			explicitHeight = explicitWidth*11/8.5;

			var g:Graphics = shape.graphics;
			g.clear();
			g.beginFill(0xFFFFFF, 0.99);
			g.drawRect(0,0,explicitWidth,explicitHeight);
			g.endFill();
			addChild(shape);	
		}
	}
}