/**
 *  D-Link 930L (and others) Motion detection enable disable
 *
 *  Author: timgray
 *  Date: 8/18/15
 *          
 */

preferences {
  input("username", "text",        title: "Username",                description: "Your Dlink camera username")
  input("password", "password",    title: "Password",                description: "Your Dlink camera password")
  input("ip",       "text",        title: "IP address/Hostname",     description: "The IP address or hostname of your Dlink camera")
  input("port",     "text",        title: "Port",                    description: "The port of your Dlink camera")
}

metadata {
  definition (name: "D-Link DCS-830L Enabler") {
    capability "Polling"
    capability "Switch"

    command "MotionOn"
    command "MotionOff"
    command "toggleMotion"
}

  tiles {
    carouselTile("cameraDetails", "device.image", width: 3, height: 2) { }

	standardTile("foscam", "device.alarmStatus", width: 1, height: 1, canChangeIcon: true, inactiveLabel: true, canChangeBackground: false) {
      state "off", label: "off", action: "toggleAlarm", icon: "st.camera.dropcam-centered", backgroundColor: "#FFFFFF"
      state "on", label: "on", action: "toggleAlarm", icon: "st.camera.dropcam-centered",  backgroundColor: "#53A7C0"
    }
	
   }

    standardTile("refresh", "device.MotionStatus", inactiveLabel: false, decoration: "flat") {
      state "refresh", action:"polling.poll", icon:"st.secondary.refresh"
    }

	
	
    main "dlink"
      details(["MotionStatus"])
  }



def toggleMotion() {
  if(device.currentValue("MotionStatus") == "on") {
    MotionOff()
  }

  else {
    MotionOn()
  }
}

def MotionOn() {
  api("set_motion", "MotionDetectionEnable=1") {
    log.debug("Motion changed to: on")
    sendEvent(name: "MotionStatus", value: "on");
  }
}

def MotionOff() {
  api("set_motion", "MotionDetectionEnable=0") {
    log.debug("Motion changed to: off")
    sendEvent(name: "MotionStatus", value: "off");
  }
}


def api(method, args = [], success = {}) {
  def methods = [
    "set_motion":      [uri: "http://${username}:${password}@${ip}:${port}/motion.cgi?${args}&ConfigReboot=No&user=${username}&pwd=${password}",    type: "get"],
    "get_params":      [uri: "http://${username}:${password}@${ip}:${port}/motion.cgi?$ConfigReboot=No&user=${username}&pwd=${password}",    type: "get"]
  ]

  def request = methods.getAt(method)

  doRequest(request.uri, request.type, success)
}

private doRequest(uri, type, success) {
  log.debug(uri)
  httpGet(uri, success)
}
