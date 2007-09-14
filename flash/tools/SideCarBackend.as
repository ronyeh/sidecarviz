package tools {
	import flash.display.LoaderInfo;
	import flash.events.DataEvent;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import java.JavaIntegration;
	
	import mx.collections.ArrayCollection;
	import mx.events.MenuEvent;
	import mx.controls.Alert;
	
	public class SideCarBackend {
		public const DEFAULT_SEARCH_TEXT:String = "type here";
		private var portNum:int;
		private var javaBackend:JavaIntegration;
		private var gui:SideCar;
		private var scrollTimer:Timer;
		private var lastItem:Object;
		private var itemNumber:Number = 0;

		private var playTimer:Timer = new Timer(500);
		private var isPlaying:Boolean = false;

		// the array storing the data for the lower pane
		private var printlnData:ArrayCollection = new ArrayCollection();

		public function SideCarBackend(ui:SideCar):void {
			gui = ui;
			// start the communication with Java
			processParameters();
			start();
			gui.printlnGrid.dataProvider = printlnData;
			scrollTimer = new Timer(200);
			scrollTimer.addEventListener(TimerEvent.TIMER, makeLastItemVisible);

			playTimer.addEventListener(TimerEvent.TIMER, playTimerHandler);
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
            var searchQuery:String = "";
            switch(msgName) {
            	case "eventHandler":
            		addData(msg.@time, msg.@component, msg.@handlerName, "");
	            	break;
	            case "googleSearch":
					searchQuery = unescape(msg.@searchQuery);
	            	trace("Google Search: " + searchQuery);
	            	break;	
	            case "googleCodeSearch":
					searchQuery = unescape(msg.@searchQuery);
	            	trace("Code Search: " + searchQuery);
	            	break;	
	            case "browsedTo":
	            	trace("Browsed To: " + msg.@url);
	            	break;	
				case "clipboardChanged":
					// trace("Clipboard Changed on Website: " + msg.@url + " to value " + msg.@contents);
					addClipboardData(msg.@url, msg.@contents);
					break;
            	default:
    	        	break;
            }
        }




		public function addClipboardData(url:String, contents:String):void {
			gui.interactionHistory.addData("Clipboard Changed", url + " : " + contents);
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

		public function addSheet(sheetName:String):void {
			
		}
		public function addRegion(regName:String, sheetName:String):void {
			
		}
		public function addHandler(handlerName:String, regName:String, sheetName:String):void {
			
		}




		// adds a state to the shelf
		public function addState():void {
			gui.shelf.numItems = gui.shelf.numItems+1;
			gui.shelf.selectedIndex = gui.shelf.numItems-1;
		}
		
		
		

		private function playTimerHandler(e:TimerEvent):void {
			gui.shelf.selectedIndex = Math.min(gui.shelf.selectedIndex + 1, gui.shelf.numItems-1);
			if (gui.shelf.selectedIndex == gui.shelf.numItems-1) {
				playTimer.stop();
			}
		}
		
		public function rewind():void {
			gui.shelf.selectedIndex = 0;
		}
		public function playOrStop():void {
			if (isPlaying) {
				playTimer.stop();
				playTimer.reset();
				gui.playStopButton.label = "Play";
			} else {
				playTimer.reset();
				playTimer.start();
				gui.playStopButton.label = "Stop";
			}
			isPlaying = !isPlaying;
		}
		public function fastForward():void {
			gui.shelf.selectedIndex = gui.shelf.numItems-1;
		}
		
		public function itemClickHandler(event:MenuEvent):void {
			Alert.show("Menu Label: " + event.label + " Item #" + event.index);
		}

		public function toggleDetailsVisibility():void {
			if (gui.systemDetails.alpha > 0) {
				gui.fOutDetails.play();
			} else {
				gui.fInDetails.play();
			}
		}
		
		public function toggleInteractionHistoryVisibility():void {
			if (gui.interactionHistory.alpha > 0) {
				gui.fOutHistory.play();
			} else {
				gui.fInHistory.play();
			}
		}

		public function endFadeOutDetails():void {
			gui.systemDetails.visible = false;
			gui.detailsButton.label = "Show System Details";
		}
		public function startFadeInDetails():void {
			gui.systemDetails.visible = true;
			gui.detailsButton.label = "Hide";
		}

		
		public function endFadeOut():void {
			gui.interactionHistory.visible = false;
			gui.historyButton.label = "Show Interaction History";
		}
		public function startFadeIn():void {
			gui.interactionHistory.visible = true;
			gui.historyButton.label = "Hide";
		}
		
		public function searchBoxFocusOut():void {
			if (gui.searchBox.text == "") {
				gui.searchBox.text = DEFAULT_SEARCH_TEXT;
				gui.searchBox.setStyle("color", "0xAAAAAA");
			}
		}
		
		public function searchBoxClicked():void {
			if (gui.searchBox.text == DEFAULT_SEARCH_TEXT) {
				gui.searchBox.text = "";
				gui.searchBox.setStyle("color", "0x222222");
			}
		}
		public function searchBoxClearClicked():void {
			gui.searchBox.text = DEFAULT_SEARCH_TEXT;
			gui.searchBox.setStyle("color", "0xAAAAAA");
		}
		public function searchBoxSubmitted():void {
			if (gui.searchBox.text != DEFAULT_SEARCH_TEXT) {
				trace("Searching for: " + gui.searchBox.text);
			}
		}
	}
}