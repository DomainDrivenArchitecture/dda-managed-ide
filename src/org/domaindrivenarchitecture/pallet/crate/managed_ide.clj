; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns org.domaindrivenarchitecture.pallet.crate.managed-ide
  (:require
    [pallet.api :as api]
    [schema.core :as s]
    [org.domaindrivenarchitecture.pallet.crate.managed-ide.base :as base]
    [org.domaindrivenarchitecture.pallet.crate.managed-ide.provider :as provider]))
  
(def facility :dda-managed-ide)
(def version  [0 1 0])
    
(def DdaIdeConfig
  "The configuration for managed ide crate." 
  {
   :provider (s/enum :aws :virtualbox)
   })

(defn default-ide-config
  "Managed ide crate default configuration"
  []
  )

(s/defn install-system
  "install common used packages for ide"
  [config :- DdaVmConfig]
  (pallet.action/with-action-options 
    {:sudo-user "root"
     :script-dir "/root/"
     :script-env {:HOME (str "/root")}}
    (base/install-xfce-desktop)
    (provider/install-specific-driver)
    ))

(s/defmethod dda-crate/dda-install facility 
  [dda-crate partial-effective-config]
  "dda managed vm: install routine"
  (let [config (dda-crate/merge-config dda-crate partial-effective-config)
        ]
    (install-system config)
    ))

(s/defmethod dda-crate/dda-test facility   
  [dda-crate partial-effective-config]
  (let [config (dda-crate/merge-config dda-crate partial-effective-config)]
    (package-res/define-resources-packages)
    (package-test/test-installed? "xfce4")
    ))

(def dda-ide-crate
  (dda-crate/make-dda-crate
    :facility facility
    :version version))

(def with-dda-ide
  (dda-crate/create-server-spec dda-ide-crate))


; as user
; sudo apt install xfce4 xfce4-goodies
; sudo apt install tightvncserver
; vncserver :1 
; vncserver -kill :1
; printf '%s\n' '#!/bin/bash' 'xrdb $HOME/.Xresources' 'startxfce4 &' > .vnc/xstartup
; vncserver
; ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -L 5901:127.0.0.1:5901 ubuntu@35.156.99.16
; gtkvncviewer