; Licensed to the Apache Software Foundation (ASF) under one
; or more contributor license agreements. See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership. The ASF licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License. You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
(ns org.domaindrivenarchitecture.cm.group
  (:require
    [pallet.api :as api]      
    [org.domaindrivenarchitecture.pallet.crate.init :as init]
    [org.domaindrivenarchitecture.pallet.crate.config :as config]
    [org.domaindrivenarchitecture.pallet.crate.managed-vm :as managed-vm]
    [org.domaindrivenarchitecture.pallet.crate.managed-ide :as managed-ide]
    [org.domaindrivenarchitecture.pallet.crate.backup :as backup]
    [org.domaindrivenarchitecture.cm.config :as ide-config]))
 
(defn managed-ide-group 
  ([login-user]
    (api/group-spec
      "managed-ide-group"
      :extends [(config/with-config ide-config/managed-ide-config) 
                init/with-init 
                managed-vm/with-dda-vm
                managed-ide/with-dda-ide
                ;backup/with-backup
                ]
      :node-spec {:image {:login-user login-user}}
      ))
  ([count node-spec]
    (api/group-spec
      "managed-ide-group"
      :extends [(config/with-config ide-config/managed-ide-config) 
                init/with-init 
                managed-vm/with-dda-vm
                managed-ide/with-dda-ide
                ;backup/with-backup
                ]
      :node-spec node-spec
      :count count))
  )
 