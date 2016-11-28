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
  {
   ;(s/optional-key provider) aws | virtualbox
   })

(defn default-ide-config
  "Managed ide crate default configuration"
  []
  )

; as user
; sudo apt install xfce4 xfce4-goodies tightvncserver
; vncserver :1 
; vncserver -kill :1
; printf '%s\n' '#!/bin/bash' 'xrdb $HOME/.Xresources' 'startxfce4 &' > .vnc/xstartup
; vncserver
; ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -L 5901:127.0.0.1:5901 ubuntu@35.156.99.16
; gtkvncviewer