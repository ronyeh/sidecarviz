package tools {
	import flash.display.LoaderInfo;
	import flash.events.DataEvent;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import java.JavaIntegration;
	
	import mx.collections.ArrayCollection;
	
	public class SideCarBackend {
		private var portNum:int;
		private var javaBackend:JavaIntegration;
		private var gui:SideCar;

		private var printlnData:ArrayCollection = new ArrayCollection();
		private var scrollTimer:Timer;
		private var lastItem:Object;

		private var itemNumber:Number = 0;

		public function SideCarBackend(ui:SideCar):void {
			gui = ui;
			// start the communication with Java
			processParameters();
			start();
			gui.printlnGrid.dataProvider = printlnData;
			scrollTimer = new Timer(200);
			scrollTimer.addEventListener(TimerEvent.TIMER, makeLastItemVisible);
		}

		
		public function addData(time:String, where:String, event:String, info:String):void {
			// convert time into a readable string...
			var date:Date = new Date();
			date.time = parseInt(time);
			trace(date + " " + where + " " + event + " " + info);
			lastItem = {Time:date, Class:where, Handler:event, PrintlnContents:info};
			printlnData.addItem(lastItem);
			// set a timer to scroll to the bottom after a second
			scrollTimer.start();
		}

		public function makeLastItemVisible(e:TimerEvent=null):void {
			scrollTimer.stop();
			gui.printlnGrid.firstVisibleItem = lastItem;
		}
		
		// retrieve parameters from the host HTML page
		private function processParameters():void {
			// for storing the parameters
			var paramObj:Object;
			try {
				var keyStr:String;
				var valueStr:String;
				paramObj = LoaderInfo(gui.root.loaderInfo).parameters;
				for (keyStr in paramObj) {
					valueStr = paramObj[keyStr] as String;
					trace(keyStr + ":\t" + valueStr);
					if (keyStr=="port") {
						portNum = parseInt(valueStr);
					}
				}
			} catch (error:Error) {
				trace(error);
			}
		}

		// this is called after the command line arguments are processed
		private function start():void {
			// toggleFullScreen();
			javaBackend = new JavaIntegration(portNum);	
			javaBackend.addMessageListener(msgListener);
		}

		// handle messages
        private function msgListener(event:DataEvent):void {
            var msg:XML = new XML(event.text);
            var msgName:String = msg.name();
            //trace(msg.toXMLString());
            switch(msgName) {
            	case "eventHandler":
            	
            		addData(msg.@time, msg.@component, msg.@handlerName, "");
	            	break;
            	default:
    	        	break;
            }
        }
	}
}