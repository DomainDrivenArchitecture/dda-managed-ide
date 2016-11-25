; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns org.domaindrivenarchitecture.pallet.crate.managed-ide
  (:require
    [pallet.api :as api]
    [schema.core :as s]
    [clojure.tools.logging :as logging]))
  
(def facility :dda-managed-ide)
(def version  [0 1 0])
    
(def DdaIdeConfig
  "The configuration for managed ide crate." 
  {})

(defn default-ide-config
  "Managed ide crate default configuration"
  []
  )

; as user
; sudo apt-get update
; sudo apt-get install ubuntu-desktop
; sudo apt-get install tightvncserver 
; vncserver :1 
; vi .vnc/xstartup
; sudo reboot now
; add a new inbound TCP rule for port 590