package tools {
	import flash.display.LoaderInfo;
	import flash.events.DataEvent;
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import ink.InkStroke;
	
	import java.JavaIntegration;
	
	import states.Page;
	import states.State;
	import states.VisualFeedback;
	
	// controls the main interaction
	public class SideCarBackend {
		public const DEFAULT_SEARCH_TEXT:String = "type here";
		private var portNum:int;
		private var javaBackend:JavaIntegration;
		private var gui:SideCar;
		private var itemNumber:Number = 0;

		private var playTimer:Timer = new Timer(450);
		private var isPlaying:Boolean = false;

		// for segmenting interactions...
		private var timeOfLastPenUp:Number = 0;

		// constructor
		public function SideCarBackend(ui:SideCar):void {
			gui = ui;
			// start the communication with Java
			processParameters();
			start();
			playTimer.addEventListener(TimerEvent.TIMER, playTimerHandler);
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
			javaBackend = new JavaIntegration(portNum);	
			javaBackend.addMessageListener(msgListener);
			javaBackend.addConnectListener(connected);
		}
		
		private function connected(e:Event):void {
			// set the name of this client
			javaBackend.sendWithArgs("setName", "FlexGUI");
		}

		public function sendMessageToSideCarServer(msg:String):void {
			javaBackend.send(msg);
		}
		public function sendMessageWithArgsToSideCarServer(msg:String, ...args):void {
			javaBackend.sendWithArgs(msg, args);
		}

		// handle messages
		// this is the core of SideCar, as we pass this data into the GUI...
        private function msgListener(event:DataEvent):void {
        	// trace(event.text);
        	
            var msg:XML = new XML(event.text);
            var msgName:String = msg.name();
            var searchQuery:String = "";
            trace("Incoming Message: " + msg.toXMLString());
            switch(msgName) {
            	case "eventHandler":
            		addSystemOutputData(msg.@component, msg.@handlerName, "");
	            	break;
	            case "googleSearch":
					searchQuery = unescape(msg.@searchQuery);
	            	trace("Google Search: " + searchQuery);
					gui.interactionHistory.addData("Search", "Web", searchQuery);
	            	break;	
	            case "googleCodeSearch":
					searchQuery = unescape(msg.@searchQuery);
	            	trace("Code Search: " + searchQuery);
					gui.interactionHistory.addData("Search", "Code", searchQuery);
	            	break;	
	            case "browsedTo":
	            	trace("Browsed To: " + msg.@url);
					gui.interactionHistory.addData("Browse To", "Web", msg.@url);
	            	break;	
	            case "copiedFromEditor":
					gui.interactionHistory.addData("Copy", "IDE", msg.@contents);
		            break;
	            case "cutFromEditor":
					gui.interactionHistory.addData("Cut", "IDE", msg.@contents);
		            break;
	            case "pasteIntoEditor":
					gui.interactionHistory.addData("Paste", "IDE", msg.@contents);
		            break;
				case "clipboardChanged":
					// trace("Clipboard Changed on Website: " + msg.@url + " to value " + msg.@contents);
					gui.interactionHistory.addData("Update Clipboard", msg.@url, msg.@contents);
					break;
				case "currentlyEditing":
					gui.interactionHistory.addData("Edit File", msg.@fileName);
					break;
				case "penDown":
					// track the time here, and add a new page if appropriate
					// 3.5 secs have passed
					// TODO: We should also add a new page if the ink is sufficiently far from the last ink sample
					if (new Date().time - timeOfLastPenUp > 3500) { 
						addPageState();
						gui.systemInternals.penDown(parseInt(msg.@penID), msg.@x, msg.@y, true);
					} else {
						gui.systemInternals.penDown(parseInt(msg.@penID), msg.@x, msg.@y);
					}
					break;
				case "penUp":
					timeOfLastPenUp = new Date().time;
					var stroke:InkStroke = gui.systemInternals.penUp(parseInt(msg.@penID), msg.@x, msg.@y);
					gui.shelf.addInkStroke(stroke);
					break;
				case "penSample":
					gui.systemInternals.penSample(parseInt(msg.@penID), msg.@x, msg.@y);
					break;
				case "debugOutput":
					trace("DebugOutput: " + msg.@value + " at " + msg.@location);
					addSystemOutputData(msg.@location, "Debug Output", msg.@value);
					break;
				case "addHandler": // will automatically add regions and sheets!
					addHandler(msg.@handlerName, msg.@regionName, msg.@sheetName);
					break;
				case "toolkitConnected":
					gui.interactionHistory.addData("PaperToolkit Started", "");
					break;
				case "applicationStarted":
					gui.interactionHistory.addData("Application Started", msg.@appName);
					break;
            	default:
		            trace("Unhandled: " + msg.toXMLString());
    	        	break;
            }
        }

		public function setStatusMessage(str:String):void {
			gui.statusBar.text = str;
		}

		// this represents the output of the system... as opposed to the interaction history pane, which contains input from the developer
		private function addSystemOutputData(where:String, event:String, info:String):void {
			gui.interactionHistory.addData(event, where, info);
		}

		// populate the gui with our paper UI information
		public function addHandler(handlerName:String, regName:String, sheetName:String):void {
			trace("Adding Handler: " + handlerName + " " + regName + " " + sheetName);
			gui.systemInternals.addHandlerItem(handlerName, regName, sheetName);
		}

		// adds a state to the shelf
		public function addVisualOutputState():void {
			var state:State = new VisualFeedback();
			gui.shelf.addItem(state);
		}
		public function addPageState():void {
			var state:State = new Page();
			gui.shelf.addItem(state);
		}
		
		
		

		private function playTimerHandler(e:TimerEvent):void {
			gui.shelf.selectedIndex = Math.min(gui.shelf.selectedIndex + 1, gui.shelf.numItems-1);
			if (gui.shelf.selectedIndex == gui.shelf.numItems-1) {
				if (isPlaying) {
					playOrStop();
				}
			}
		}
		public function rewind():void {
			gui.shelf.selectedIndex = 0;
		}
		// toggles between the two states...
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
		public function get currentlyPlaying():Boolean {
			return isPlaying;
		}
		

		public function toggleDetailsVisibility():void {
			if (gui.systemInternals.alpha > 0) {
				gui.fOutDetails.play();
			} else {
				gui.fInDetails.play();
			}
		}
		

		public function endFadeOutDetails():void {
			gui.systemInternals.visible = false;
			gui.detailsButton.label = "Show System Details";
		}
		public function startFadeInDetails():void {
			gui.systemInternals.visible = true;
			gui.detailsButton.label = "Hide";
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