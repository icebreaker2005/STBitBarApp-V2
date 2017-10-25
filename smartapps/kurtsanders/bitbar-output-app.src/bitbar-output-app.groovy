/**
 *  BitBar Output App
 *
 *  Copyright 2017 Kurt Sanders
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

 // V 1.0 Initial release by Matt (V1)
 // V 1.1 Added logging from @kurtsanders making it easier to copy/paste URL and secret
 // V 1.2 Add extra handling if Main Display is not set (right now N/A is displayed)
 // V 1.3 Add Lock capability support
 // V 1.4 Add Thermostat selection and battery data output
 // V 1.5 Add Thermostat control options and version verification
 // V 1.6 Merge changes from @kurtsanders adding presence and motion capability
 // V 1.7 Added Routine changes capability from @kurtsanders
 // V 1.8 Add multiple thermostat support
 // V 2.0 Application Migrated & Converted to Use KurtSanders GitHub after repeatedly asking
 //       original developer to pull merge requests without any responses
 // V 2.0 Moved all MacOS display options to the SmartApp, Added Sort by Activity, Changed Version Logic,
 //       Added 'About' SubMenu for Version Checking
 //       Local config file only requires the SmartThings URL and Secret
 // V 2.1 Added Auto Active Device SubMenu Switch Option
 // V 2.2 Added Support for Extended Ascii and Unicode Characters
 // V 2.22 Added Option for Compact Display of Submenu (No Repeat Header)
 // V 2.23 Revised Battery Display from N/A to blank
 // V 2.24 Added Event History Sub Menu to EaCH Presence Sensor
 // V 2.25 Added Capability for User Control Fonts, Pitch and Color
 // V 2.26 Added Manual or Dynamic Auto-sizing Device Categories by Each Category
 // V 2.27 Revised how Thermostat information is displayed in menu bar when in Auto mode
 // V 2.28 Added Horizontal Separator Bar Option in the GUI
 // V 2.30 Added History Events to selected devices, modified GUI with Event options
 //        Added Support for Extended Ascii in Modes and Routine Names
 //        Added Additional Control of Font Names, Colors and Sizes used in BitBar display
 //        Added Battery Warning Emoji Level
 //        Better formatting of the API URL and Secret displayed in the ST Live Logging Screen and GUI

// Major BitBar Version requires a change to the Python Version, Minor BitBar Version numbering will still be compatible with lower minor Python versions
// Example:  BitBar 2.0, 3.0, 4.0  Major Releases (Requires both ST Code and Python to be upgraded to the same major release number)
//           BitBar 2.1, 2.2, 2.21 Minor releases (No change needed to the Python Code on MacOS if same Python major release number)
def version() { return "2.30" } // Must be a Floating Number String "2.1", "2.01", "2.113"
def colorChoiceList() { return ["lightseagreen","floralwhite","lightgray","darkgoldenrod","paleturquoise","goldenrod","skyblue","indianred","darkgray","khaki","blue","darkred","lightyellow","midnightblue","chartreuse","lightsteelblue","slateblue","firebrick","moccasin","salmon","sienna","slategray","teal","lightsalmon","pink","burlywood","gold","springgreen","lightcoral","black","blueviolet","chocolate","aqua","darkviolet","indigo","darkcyan","orange","antiquewhite","peru","silver","purple","saddlebrown","lawngreen","dodgerblue","lime","linen","lightblue","darkslategray","lightskyblue","mintcream","olive","hotpink","papayawhip","mediumseagreen","mediumspringgreen","cornflowerblue","plum","seagreen","palevioletred","bisque","beige","darkorchid","royalblue","darkolivegreen","darkmagenta","orange red","lavender","fuchsia","darkseagreen","lavenderblush","wheat","steelblue","lightgoldenrodyellow","lightcyan","mediumaquamarine","turquoise","dark blue","darkorange","brown","dimgray","deeppink","powderblue","red","darkgreen","ghostwhite","white","navajowhite","navy","ivory","palegreen","whitesmoke","gainsboro","mediumslateblue","olivedrab","mediumpurple","darkslateblue","blanchedalmond","darkkhaki","green","limegreen","snow","tomato","darkturquoise","orchid","yellow","green yellow","azure","mistyrose","cadetblue","oldlace","gray","honeydew","peachpuff","tan","thistle","palegoldenrod","mediumorchid","rosybrown","mediumturquoise","lemonchiffon","maroon","mediumvioletred","violet","yellow green","coral","lightgreen","cornsilk","mediumblue","aliceblue","forestgreen","aquamarine","deepskyblue","lightslategray","darksalmon","crimson","sandybrown","lightpink","seashell"].sort()}

definition(
    name: "BitBar Output App",
    namespace: "kurtsanders",
    author: "kurtsanders",
    description: "Display SmartThings Device Information in the Apple BitBar (OSX) Application",
    category: "My Apps",
    iconUrl:    "https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/Images/STBitBarApp-V2-60.png",
    iconX2Url:  "https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/Images/STBitBarApp-V2-120.png",
    iconX3Url:  "https://raw.githubusercontent.com/KurtSanders/STBitBarApp-V2/master/Images/STBitBarApp-V2-120.png")

preferences {
  page(name:"mainPage")
  page(name:"devicesPage")
  page(name:"optionsPage")
  page(name:"fontsPage")
  page(name:"iconsPage")
  page(name:"categoryPage")
  page(name:"eventsPage")
  page(name:"disableAPIPage")
  page(name:"enableAPIPage")
  page(name:"APIPage")  
  section ("Display Sensor") {
  input "displayTempName", "string", multiple: false, required: false
    input "displayTemp", "capability.temperatureMeasurement", multiple: false, required: false
  }
  section ("Allow external service to get the temperature ...") {
    input "temps", "capability.temperatureMeasurement", multiple: true, required: false
  }
  section ("Allow external service to get the contact status ...") {
    input "contacts", "capability.contactSensor", multiple: true, required: false
  }
  section ("Allow external service to get the switches ...") {
    input "switches", "capability.switch", multiple: true, required: false
  }
  section ("Allow external service to get the thermostat status ...") {
    input "thermos", "capability.thermostat", multiple: true, required: false
  }
  section ("Allow external service to get the motion status ...") {
    input "motions", "capability.motionSensor", multiple: true, required: false
  }
  section ("Allow external service to get the presence status ...") {
    input "presences", "capability.presenceSensor", multiple: true, required: false
  }
}
mappings {

  path("/GetStatus/") {
    action: [
      GET: "getStatus"
    ]
  }
  path("/ToggleSwitch/") {
    action: [
      GET: "toggleSwitch"
    ]
  }
  path("/SetLevel/") {
    action: [
      GET: "setLevel"
    ]
  }
    path("/SetThermo/") {
    action: [
      GET: "setThermo"
    ]
  }
    path("/SetRoutine/") {
    action: [
      GET: "setRoutine"
    ]
  }
    path("/SetMode/") {
    action: [
      GET: "setMode"
    ]
  }
  path("/ToggleLock/") {
    action: [
      GET: "toggleLock"
    ]
  }
}
def installed() {
//	log.debug "Installed with settings: ${settings}\n"
	initialize()
}
def uninstalled() {
	if (state.endpoint) {
		try {
			log.debug "Revoking API access token"
			revokeAccessToken()
		}
		catch (e) {
			log.warn "Unable to revoke API access token: $e"
		}
	}
}
def updated() {
	// Added additional logging from @kurtsanders
    log.debug "##########################################################################################"
    log.debug "secret=${state.endpointSecret}"
    log.debug "smartAppURL=${state.endpointURL}"
    log.debug "##########################################################################################"
    log.debug "The API has been setup. Please enter the next two strings exactly as shown into the ST_Python_Logic.cfg file which is in your BitBar plugins directory."
    log.debug "##########################################################################################"

	unsubscribe()
	initialize()
}

def initialize() {
	if(thermos)
    	thermos.each {
			subscribe(it, "thermostatOperatingState", thermostatOperatingStateHandler)
        }
    state.lastThermostatOperatingState = now()
    state.eventsDays = eventsDays
    state.eventsMax = eventsMax
}
def thermostatOperatingStateHandler(evt) {
	log.debug "thermostatOperatingStateHandler received event"
	state.lastThermostatOperatingState = now()
}


// Respond to action requests
def setRoutine() {
	log.debug "Begin: setRoutine"
    def command = params.id
    log.debug "CurrentMode Before setRoutine : ${location.mode}"
    log.debug "setRoutine called with command: ${command}"
	location.helloHome?.execute(command)
    log.debug "CurrentMode After setRoutine  : ${location.mode}"
}

def setMode() {
	def command = params.id
    log.debug "CurrentMode Before setMode : ${location.mode}"
	log.debug "setMode called with id ${command}"
    setLocationMode(command)
    log.debug "CurrentMode After setMode  : ${location.mode}"
}

def toggleSwitch() {
	def command = params.id
	log.debug "toggleSwitch called with id ${command}"

    switches.each {
    	if(it.id == command)
        {
        	log.debug "Found switch ${it.displayName} with id ${it.id} with current value ${it.currentSwitch}"
            if(it.currentSwitch == "on")
            	it.off()
            else
            	it.on()
            return
		}
    }
    log.debug "Error! Did not find a switch with id ${command}."
}
def setLevel() {
	def command = params.id
    def level = params.level
	log.debug "setLevel called with id ${command} and level ${level}"

    switches.each {
    	if(it.id == command)
        {
        	log.debug "Found switch ${it.displayName} with id ${it.id} with current value ${it.currentSwitch}"
            def fLevel = Float.valueOf(level)
            log.debug "Setting level to ${fLevel}"
            it.setLevel(fLevel)
            return
		}
    }
    log.debug "Error: Did not find a switch with id ${command}. "
}
def setThermo() {
	def id = params.id
    def cmdType = params.type
    def val = params.val.toInteger()
	log.debug "setThermo called with id ${id} command ${cmdType} and value ${val}"

    if(thermos) {
    thermos.each {
            if(it.id == id) {
            log.debug "setThermo found id ${id}"
                if(cmdType == "mode") {
                    if(val == "auto") {
                        log.debug "Setting thermo to auto"
                        it.auto()
                    }
                    if(val == "heat") {
                        log.debug "Setting thermo to heat"
                        it.heat()
                    }
                    if(val == "cool") {
                        log.debug "Setting thermo to cool"
                        it.cool()
                    }
                    if(val == "off") {
                        log.debug "Setting thermo to off"
                        it.off()
                    }
                }
                if(cmdType == "heatingSetpoint") {
                    log.debug "Setting Heat Setpoint to ${val}"
                    it.setHeatingSetpoint(val)
                }
                if(cmdType == "coolingSetpoint") {
                    log.debug "Setting Cool Setpoint to ${val}"
                    it.setCoolingSetpoint(val)
                }
            }
        }
    }
}
def toggleLock() {
	def command = params.id
	log.debug "toggleLock called with id ${command}"

    locks.each {
    	if(it.id == command)
        {
        	log.debug "Found lock ${it.displayName} with id ${it.id} with current value ${it.currentLock}"
            if(it.currentLock == "locked")
            	it.unlock()
            else if(it.currentLock == "unlocked")
            	it.lock()
            else
            	log.debug "Non-supported toggle state for lock ${it.displayName} state ${it.currentLock} let's not do anything"
            return
		}
    }
    log.debug "Error: Did not find a lock with id ${command}."
}

def getBatteryInfo(dev) {
    if(dev.currentBattery) {
        if(state.batteryWarningPct == null || state.batteryWarningPct < 0 || state.batteryWarningPct > 100 ) {
            state.batteryWarningPct = 50
        }
        if(dev.currentBattery <= state.batteryWarningPct){
            return [dev.currentBattery, batteryWarningPctEmoji == null?" :grimacing: ":" " + batteryWarningPctEmoji + " "]
        }
        return [dev.currentBattery, ""]
    }
    else return "N/A"
}

// Respond to data requests
def getTempData() {
//	log.debug "getTemps called"
	def resp = []
    temps.each {
        resp << [name: it.displayName, value: it.currentTemperature, battery: getBatteryInfo(it)];
    }
//    log.debug resp
    // Sort decending by temp value
    resp.sort { -it.value }
//    log.debug "getTemps complete"
    return resp
}
def getContactData() {
	def resp = []
    contacts.each {
        resp << [name: it.displayName, value: it.currentContact, battery: getBatteryInfo(it)];
    }
    return resp
}
def getPresenceData() {
	def resp = []
    def eventlogData = []
    def timeFormatString = timeFormatBool?'EEE MMM dd HH:mm z':'EEE MMM dd hh:mm a z'
    presences.each {
		it.events().each {
//        	eventlogData << [date: it.date.format(timeFormatString , location.timeZone), value: it.description]
//        	log.debug "name: ${it.displayName}, Date: ${it.date.format('M-d-yyyy h:mm:ss a', location.timeZone)}, Raw Value: ${it.description}, Value: ${it.description=="presence: 1"?"Present":"Not Present"}"
        }
        resp << [name: it.displayName, value: it.currentPresence, battery: getBatteryInfo(it), eventlog: getEventsOfDevice(it)];
//        resp << [name: it.displayName, value: it.currentPresence, battery: getBatteryInfo(it), eventlog: eventlogData];
//        log.debug "${it.displayName}: ${eventlogData}"
//        eventlogData = []
    }
    return resp
}
def getMotionData() {
	def resp = []
    motions.each {
        resp << [name: it.displayName, value: it.currentMotion, battery: getBatteryInfo(it), eventlog: getEventsOfDevice(it)];
    }
    return resp
}
def getSwitchData() {
	def resp = []
    switches.each {
    	def isDimmer = false
        def currentName = it.displayName
    	if(it.currentLevel) {
        	isDimmer = true
            if(it.currentSwitch == 'on') currentName += " (" + it.currentLevel + "%)"
        }

        resp << [name: currentName, value: it.currentSwitch, id : it.id, isDimmer : isDimmer];
    }
    return resp
}
def getLockData() {
	def resp = []
    locks.each {
        resp << [name: it.displayName, value: it.currentLock, id : it.id, battery: getBatteryInfo(it)];
    }
    return resp
}
def getThermoData() {

	def resp = []
    if(thermos) {
        thermos.each {
            def timespan = now() - state.lastThermostatOperatingState
            resp << [displayName: it.displayName,
                    id: it.id,
                    thermostatOperatingState: it.currentThermostatOperatingState,
                    thermostatMode: it.currentThermostatMode,
                    coolingSetpoint: it.currentCoolingSetpoint,
                    heatingSetpoint: it.currentHeatingSetpoint,
                    lastOperationEvent: timespan
                    ];
        }
    }
//    log.debug "getThermoData: ${resp[2]}"
    return resp
}
def getMainDisplayData() {
	def returnName;
    def returnValue;

    if(displayTempName) returnName = displayTempName
    else returnName = "N/A"
    if(displayTemp) returnValue = displayTemp.currentTemperature
    else returnValue = "N/A"

	def resp = []
    resp << [name: returnName, value: returnValue];
    return resp
}
def getStatus() {
    log.debug "getStatus called"
    def pythonAppVersion = params.pythonAppVersion
    def pythonAppPath = params.path
	log.debug "getStatus called with version ${pythonAppVersion} and path ${pythonAppPath}"
    state.pythonAppVersion = pythonAppVersion
    state.pythonAppPath = pythonAppPath

    def tempData = getTempData()
    def contactData = getContactData()
    def presenceData = getPresenceData()
    def motionData = getMotionData()
    def switchData = getSwitchData()
    def lockData = getLockData()
    def thermoData = getThermoData()
    def mainDisplay = getMainDisplayData()
    def optionsData = [ "useImages" 				: useImages,
                       "sortSensorsName" 			: sortSensorsName,
                       "sortSensorsActive" 			: sortSensorsActive,
                       "mainMenuMaxItemsTemps" 		: mainMenuMaxItemsTemps,
                       "mainMenuMaxItemsContacts" 	: mainMenuMaxItemsContacts,
                       "mainMenuMaxItemsSwitches" 	: mainMenuMaxItemsSwitches,
                       "mainMenuMaxItemsMotion" 	: mainMenuMaxItemsMotion,
                       "mainMenuMaxItemsLocks" 		: mainMenuMaxItemsLocks,
                       "showSensorCount"			: showSensorCount,
                       "mainMenuMaxItemsPresences" 	: mainMenuMaxItemsPresences,
                       "motionActiveEmoji"			: motionActiveEmoji,
                       "motionInactiveEmoji"		: motionInactiveEmoji,
                       "contactOpenEmoji"		 	: contactOpenEmoji,
                       "contactClosedEmoji"		 	: contactClosedEmoji,
                       "presenscePresentEmoji"		: presenscePresentEmoji,
                       "presensceNotPresentEmoji"	: presensceNotPresentEmoji,
                       "presenceDisplayMode"		: presenceDisplayMode,
                       "numberOfDecimals"			: numberOfDecimals,
                       "matchOutputNumberOfDecimals": matchOutputNumberOfDecimals,
                       "mainFontName"				: mainFontName,
                       "mainFontSize"				: mainFontSize,
                       "subMenuFontName"			: subMenuFontName,
                       "subMenuFontSize"			: subMenuFontSize,
                       "subMenuFontColor"			: subMenuFontColor,
                       "subMenuMoreColor"			: subMenuMoreColor,
                       "subMenuCompact"				: subMenuCompact,
                       "fixedPitchFontName"			: fixedPitchFontName,
                       "fixedPitchFontSize"			: fixedPitchFontSize,
                       "fixedPitchFontColor"		: fixedPitchFontColor,
                       "hortSeparatorBarBool"       : hortSeparatorBarBool,
                       "batteryWarningPct"			: batteryWarningPct,
                       "batteryWarningPctEmoji"		: batteryWarningPctEmoji
                      ]
    def resp = [ "Version" : version(),
                "Temp Sensors" : tempData,
                "Contact Sensors" : contactData,
                "Presence Sensors" : presenceData,
                "Motion Sensors" : motionData,
                "Switches" : switchData,
                "Locks" : lockData,
                "Thermostats" : thermoData,
                "Routines" : location.helloHome?.getPhrases()*.label,
                "Modes" : location.modes,
                "CurrentMode" : ["name":location.mode],
                "MainDisplay" : mainDisplay,
                "Options" : optionsData]

    log.debug "getStatus complete"
    return resp
}

private mainPage() {
	dynamicPage(name: "mainPage", uninstall:true, install:true) {
        section("Version Information") {
        	paragraph "ST BitBar Output SmartApp Version: ${version()}" +
            "${state.pythonAppVersion == null?"\nST_Python_Logic.py Version: Not Installed":"\nST_Python_Logic.py Version: " + state.pythonAppVersion}" +
            "${state.pythonAppPath    == null?"\nBitBar Plugin Directory: Not Installed":"\nBitBar Plugin Directory: "    + state.pythonAppPath}"
        }
        section("API Setup") {
        href name: "APIPageLink", title: "API Setup", description: "", page: "APIPage"
        }
        section("Device Setup") {
        href name: "devicesPageLink", title: "Select Devices", description: "", page: "devicesPage"
        }
        section("Apple Menu BitBar Output Display Options") {
        href name: "optionsPageLink", title: "BitBar Output Menu Display Options", description: "", page: "optionsPage"
        }
	}
}


def disableAPIPage() {
	dynamicPage(name: "disableAPIPage", title: "") {
		section() {
			if (state.endpoint) {
				try {
					revokeAccessToken()
				}
				catch (e) {
					log.debug "Unable to revoke access token: $e"
				}
				state.endpoint = null
			}
			paragraph "It has been done. Your token has been REVOKED. You're no longer allowed in API Town (I mean, you can always have a new token). Tap Done to continue."
		}
	}
}

def APIPage() {
    dynamicPage(name: "APIPage") {
        section("API Setup") {
            if (state.endpoint) {
                paragraph "API has been setup. Please enter the following two strings in your 'ST_Python_Logic.cfg' file in your Apple Mac BitBar Plugins directory."
                paragraph "smartAppURL=${state.endpointURL}"
                paragraph "secret=${state.endpointSecret}"
                href "disableAPIPage", title: "Disable API (Only use this if you want to generate a new secret)", description: ""
            }
            else {
                paragraph "Required: The API has not been setup. Tap below to enable it."
                href name: "enableAPIPageLink", title: "Enable API", description: "", page: "enableAPIPage"
            }
        }
    }
}

def enableAPIPage() {
	dynamicPage(name: "enableAPIPage") {
		section() {
			if (initializeAppEndpoint()) {
				paragraph "Woo hoo! The API is now enabled. Brace yourself, though. I hope you don't mind typing long strings of gobbledygook. Sorry I don't know of an easier way to transfer this to the PC. Anyways, tap Done to continue"
			}
			else {
				paragraph "It looks like OAuth is not enabled. Please login to your SmartThings IDE, click the My SmartApps menu item, click the 'Edit Properties' button for the BitBar Output App. Then click the OAuth section followed by the 'Enable OAuth in Smart App' button. Click the Update button and BAM you can finally tap Done here.", title: "Looks like we have to enable OAuth still", required: true, state: null
			}
		}
	}
}

def devicesPage() {
	dynamicPage(name:"devicesPage") {

        section("Mac Menu Bar: Status Bar Title Device") {
        paragraph "Enter the short {friendly} name for the device you want displayed as the main status bar item and choose the device"
			input "displayTempName", "string",
				title: "Main Menu Bar Display: Name (This field can be left blank and the menu bar will display only be the temperature value)",
				multiple: false,
				required: false
			input "displayTemp", "capability.temperatureMeasurement",
				title: "Main Menu Bar Display: Tempertaure Sensor",
				multiple: false,
				hideWhenEmpty: true,
				required: false
        }

		section ("Choose Devices") {
			paragraph "Select devices that you want to be displayed below the Main Menu Bar (in the BitBar Sub-menu)."
			input "temps", "capability.temperatureMeasurement",
				title: "Which Temperature Sensors?",
				multiple: true,
				hideWhenEmpty: true,
				required: false
			input "contacts", "capability.contactSensor",
				title: "Which Contact Sensors?",
				multiple: true,
				hideWhenEmpty: true,
				required: false
			input "motions", "capability.motionSensor",
				title: "Which Motion Sensors?",
				multiple: true,
				hideWhenEmpty: true,
				required: false
			input "switches", "capability.switch",
				title: "Which Switches?",
				multiple: true,
				hideWhenEmpty: true,
				required: false
			input "locks", "capability.lock",
				title: "Which Locks?",
				multiple: true,
				hideWhenEmpty: true,
				required: false
			input "presences", "capability.presenceSensor",
				title: "Which Presence Sensors?",
				multiple: true,
				hideWhenEmpty: true,
				required: false
			input "thermos", "capability.thermostat",
				title: "Which Thermostats?",
				multiple: true,
				hideWhenEmpty: true,
				required: false
		}
	}
}

def iconsPage() {
    dynamicPage(name:"iconsPage", hideWhenEmpty: true) {
        section("BROWSE Emoji Website Valid 'ShortCodes' List") {
            href(name: "hrefNotRequired",
                 title: "BROWSE Emoji Website Valid 'ShortCodes' List",
                 required: false,
                 image: "http://emojipedia.org/static/img/favicons/mstile-144x144.png",
                 style: "external",
                 url: "http://www.webpagefx.com/tools/emoji-cheat-sheet/",
                 description: "tap here to view valid list of Emoji names in your mobile browser"
                )
        }                
        section("Optional: Sensor Status Icon Display Options") {
            input "motionActiveEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Motion Sensor = 'Active' Default='⇠⇢'",
                required: false
            input "motionInactiveEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Motion Sensor = 'Inactive' Default='⇢⇠'",
                required: false
            input "contactOpenEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Contact Sensor = 'Open' Default='⇠⇢'",
                required: false
            input "contactClosedEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Contact Sensor = 'Closed' Default='⇢⇠'",
                required: false
            input "presenscePresentEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Presence Sensor = 'Present' Default=':home:'",
                required: false
            input "presensceNotPresentEmoji", "text",
                title: "Emoji ShortCode ':xxx:' to Display for Presence Sensor = 'Not Present' Default=':x:'",
                required: false
            input "presenceDisplayMode", "enum",
                title: "Presence Display Mode Number (See Numeric Values Explanation Below)",
                multiple: false,
                options: [0,1,2,3],
                required: false
            paragraph image: "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Emblem-notice.svg/2000px-Emblem-notice.svg.png",
                title: "Presence Display Mode",
                "0 = Default Mode: Do not sort or hide sensors by presence status\n" +
                "1 = Sort by Presence: Sensors will be sorted by their status; 'Present' shown first\n" +
                "2 = Show Sensors Not Present in Submenu: All sensors that are not present will be moved to a submenu\n" +
                "3 = Hide Sensors Not Present: Sensors that are currently not present will be hidden"
        }
    }
}

def fontsPage() {
    dynamicPage(name:"fontsPage", hideWhenEmpty: true) {
        section("Link: BROWSE List of VALID typeface names included with Apple macOS") {
            href(name: "hrefNotRequired",
                 title: "BROWSE List of typeface names included with Apple macOS",
                 required: false,
                 image: "http://iconion.com/posts/images/scale-to-size-icons.jpg",
                 style: "external",
                 url: "https://en.wikipedia.org/wiki/List_of_typefaces_included_with_macOS",
                 description: "Tap this area to view a list of typeface names included with Apple macOS in your mobile browser")
        }
        section("Optional Main Menu: DISPLAY FONTS, COLORS & SEPARATOR BARS: Mac Font Name for Display (warning: The Font Name MUST exist on the Mac.  Leave blank for 'Menlo'") {
            input "mainFontName", "text",
                title: "Mac 'Main-Menu Bar' Font Name (default font is 'Menlo' if field is left empty).  Color auto-changes based on Primary Thermostat Operation Mode (Heat/Cool) from/to Red/Blue",
                default: "Menlo",
                required: false
            input "mainFontSize", "number",
                title: "Mac 'Main-Menu Bar' Font Pitch Size (default is '14').",
                default: 14,
                required: false
        }
        section("Optional Data Display: DISPLAY FONTS, COLORS & SEPARATOR BARS: Mac Font Name for Display (warning: The Font Name MUST exist on the Mac.  Leave blank for 'Menlo'") {
            input "fixedPitchFontName", "enum",
                title: "Data: Fixed Font Name (default font is 'Menlo' if field is left empty).",
                default: "Menlo",
                options: ["Menlo","Monaco","Consolas","Courier","MingLIU"],                
                required: false
            input "fixedPitchFontSize", "number",
                title: "Data: Fixed Font Pitch Size (default is '14').",
                default: 14,
                required: false
            input "fixedPitchFontColor", "enum",
                title: "Data: Fixed Font Color (default Color is 'Black' if field left blank.  If Mac is in 'Dark Mode', Font Color will be set to 'White')",
                default: "black",
				options: colorChoiceList(),
                multiple: false,
                required: false
        }
        section("Sub-Menu Display Options") {
            input "subMenuCompact", "bool",
                title: "Compact Sub-Menus (No repeating title in the submenu header or breakout menus)",
                required: false
            input "hortSeparatorBarBool", "bool",
                title: "Use Horizontal Separator Lines to Separate ST Device Categories (Default is True)",
                required: false
            input "subMenuFontName", "text",
                title: "Mac BitBar Sub-Menu ST Category Names: Font Name (default font is 'Menlo' if field is left empty).",
                default: "Menlo",
                required: false
            input "subMenuFontSize", "number",
                title: "Mac BitBar Sub-Menu ST Category Names: Font Pitch Size (default is '12').",
                default: 12,
                required: false
            input "subMenuFontColor", "enum",
                title: "Mac BitBar Sub-Menu ST Category Names: Font Color (default Color is 'Black' if field left blank.  If Mac is in 'Dark Mode', Font Color will be set to 'White')",
                default: "black",
                options: colorChoiceList(),
                multiple: false,
                required: false
            input "subMenuMoreColor", "enum",
                title: "More... sub-menu: Font Color (default 'black')",
                default: "black",
                options: colorChoiceList(),
                multiple: false,
                required: false
        }
    }
}

def categoryPage() {
    dynamicPage(name:"categoryPage", hideWhenEmpty: true) {
        section("Optional: Number of Items per ST Sensor category to display in the Main Menu before moving additional sensors to a More... sub-menu.  Leave these number fields blank for Auto-Size (Show only Active Sensors in Main Menu)") {
            input "mainMenuMaxItemsTemps", "number",
                title: "Min Number of Temperature Sensors to Display - Leave blank to Show All Temperature Sensors",
                required: false
            input "mainMenuMaxItemsContacts", "number",
                title: "Min Number of Contact Sensors to Display - Leave blank to Auto-Size",
                required: false
            input "mainMenuMaxItemsSwitches", "number",
                title: "Min Number of Switch Sensors to Display - Leave blank to Auto-Size",
                required: false
            input "mainMenuMaxItemsMotion", "number",
                title: "Min Number of Motion Sensors to Display - Leave blank to Auto-Size",
                required: false
            input "mainMenuMaxItemsLocks", "number",
                title: "Min Number of Locks to Display - Leave blank to Auto-Size",
                required: false
            input "mainMenuMaxItemsPresences", "number",
                title: "Min Number of Presence Sensors to Display - Leave blank to Auto-Size",
                required: false
        }
    }
}

def eventsPage() {
    dynamicPage(name:"eventsPage", hideWhenEmpty: true) {
        section("Optional: Event History Options for Devices") {
            input "eventsMax", "number",
                title: "Events: Maximum number of Device Events to Display [Default: 25, Max 100]",
                default: "25",
                required: false
            input "eventsDays", "number",
                title: "Events: Number of Days from Today to Retrieve Device Events [Default: 1, Max 7]",
                default: "1",
                required: false
            input "eventsTimeFormat", "enum",
                title: "Events: DateTime Format: [Default = 12 Hour Clock Format with AM/PM]",
                options: ["12 Hour Clock Format with AM/PM", "Military 24 Hour Clock Format"],
                default: "12 Hour Clock Format with AM/PM",
                required: true
        }
        section("BROWSE Emoji Website Valid 'ShortCodes' List") {
            href(name: "hrefNotRequired",
                 title: "BROWSE Emoji Website Valid 'ShortCodes' List",
                 required: false,
                 image: "http://emojipedia.org/static/img/favicons/mstile-144x144.png",
                 style: "external",
                 url: "http://www.webpagefx.com/tools/emoji-cheat-sheet/",
                 description: "tap here to view valid list of Emoji names in your mobile browser"
                )
                }
        section("Optional: Battery Level Options for Devices") {                
            input "batteryWarningPctEmoji", "text",
                title: "Battery: Emoji ShortCode ':xxx:' to Display for Low Battery Warning [Default=':grimacing:']",
                required: false
            input "batteryWarningPct", "number",
                title: "Battery: Percent Level to Warn for Low Battery in Device [Default: 50, Min 0, Max 100]",
                default: "50",
                required: false
        }
    }
}

def optionsPage() {
	dynamicPage(name:"optionsPage", hideWhenEmpty: true) {
        section("Required: Images or Emojis for switch & lock status") {
			input "useImages", "bool",
				title: "Use images for switch & lock status (green/red images) instead of emojis",
				required: true
			input "showSensorCount", "bool",
				title: "Show the number of sensors in the sensor category title",
				required: true
        }
        section("Required: Sort sensors in menu by Name and Active Status") {
			input "sortSensorsName", "bool",
				title: "Sort sensors in menu by name",
				required: true
			input "sortSensorsActive", "bool",
				title: "Sort sensors in menu by Active Status",
				required: true
        }
        section("Optional: Temperature Values Display Options") {
            input "numberOfDecimals", "number",
                title: "Round temperature values with the following number of decimals",
                required: true
            input "matchOutputNumberOfDecimals", "bool",
                title: "Match all temperature sensor values with the same amount of decimals",
                required: true
        }
        section("Optional: Number of Items per ST Sensor category to display Settings") {
            href name: "categoryPageLink", title: "Sensor Status Icon Display Options", description: "", page: "categoryPage"
        }
        section("Optional: Event History and Battery Level Options for Devices") {
            href name: "eventsPageLink", title: "Event History and Battery Level Options", description: "", page: "eventsPage"
        }
        section("Optional: Sensor Status Icon Display Options") {
            href name: "iconsPageLink", title: "Sensor Status Icon Display Settings", description: "", page: "iconsPage"
        }
        section("Optional: Font Names, Pitch Size and Colors") {
            href name: "fontsPageLink", title: "BitBar Output Menu Text Display Settings", description: "", page: "fontsPage"
        }
    }
}

private initializeAppEndpoint() {
	if (!state.endpoint) {
		try {
			def accessToken = createAccessToken()
			if (accessToken) {
				state.endpoint = apiServerUrl("/api/token/${accessToken}/smartapps/installations/${app.id}/")
                state.endpointURL = apiServerUrl("/api/smartapps/installations/${app.id}/")
                state.endpointSecret = accessToken
			}
		}
		catch(e) {
			state.endpoint = null
		}
	}
	return state.endpoint
}

def getEventsOfDevice(device) {
//   log.debug "Start: state.eventsDays = ${state.eventsDays}"
//   log.debug "Start: state.eventsMax = ${state.eventsMax}"
    if (state.eventsDays==null) {
    	state.eventsDays = 7
//        log.debug "Null: state.eventsDays = ${state.eventsDays}"
        }
    if (state.eventsMax==null) {
    	state.eventsMax = 100
//        log.debug "Null: state.eventsMax = ${state.eventsMax}"
        }
    if (state.eventsDays > 7) {state.eventsDays = 7}
    if (state.eventsMax > 100) {state.eventsMax = 100}

//    log.debug "StartQuery: state.eventsDays = ${state.eventsDays}"
//    log.debug "StartQuery: state.eventsMax = ${state.eventsMax}"
	def today = new Date()
	def then = timeToday(today.format("HH:mm"), TimeZone.getTimeZone('UTC')) - state.eventsDays
    def timeFormatString = eventsTimeFormat=="12 Hour Clock Format with AM/PM"?'EEE MMM dd hh:mm a z':'EEE MMM dd HH:mm z'
	device.eventsBetween(then, today, [max: state.eventsMax])?.findAll{"$it.source" == "DEVICE"}?.collect{[date: it.date.format(timeFormatString , location.timeZone), name: it.name, value: it.value]}
}