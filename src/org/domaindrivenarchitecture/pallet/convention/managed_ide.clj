; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns org.domaindrivenarchitecture.pallet.convention.managed-ide
  (:require
    [schema.core :as s]
    [org.domaindrivenarchitecture.config.commons.map-utils :as map-utils]
    [org.domaindrivenarchitecture.pallet.crate.managed-ide :as crate]
    [org.domaindrivenarchitecture.pallet.crate.managed-vm :as vm-crate]
    [org.domaindrivenarchitecture.pallet.crate.backup :as backup-crate]
    [org.domaindrivenarchitecture.pallet.convention.managed-vm :as vm-convention]))

(def DdaIdeConventionConfig
  "The convention configuration for managed vms crate."
  (merge
    vm-convention/DdaVmConventionConfig
    {:dev-platform (s/enum :clojure)}))

(s/defn default-ide-backup-config :- backup-crate/BackupConfig
  "Managed vm crate default configuration"
  [user-key :- s/Keyword]
  (vm-convention/default-vm-backup-config user-key))

(s/defn default-ide-config :- crate/DdaIdeConfig
  "Managed vm crate default configuration"
  [user-name dev-platform vm-platform]
  (map-utils/deep-merge 
    {:ide-user user-name}
    (cond 
      (= dev-platform :clojure) {:clojure {:os-user-name user-name}
                                 :atom {:settings (if (= vm-platform :aws) 
                                                    #{:install-aws-workaround}
                                                    #{})}
                                 })        
    ))

(s/defn ide-convention :- {:dda-managed-ide crate/DdaIdeConfig
                           :dda-managed-vm vm-crate/DdaVmConfig
                           :dda-backup backup-crate/BackupConfig}
  [convention-config :- DdaIdeConventionConfig]
  (let [user-key (:ide-user convention-config)
        user-name (name user-key)
        vm-platform (:vm-platform convention-config)
        dev-platform (:dev-platform convention-config)]  
    {:dda-managed-ide (default-ide-config user-name dev-platform vm-platform)
     :dda-managed-vm (vm-convention/default-vm-config user-key vm-platform)
     :dda-backup (default-ide-backup-config user-key)}
  ))
