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

(ns dda.pallet.dda-managed-ide.infra.atom
  (:require
    [schema.core :as s]
    [clojure.tools.logging :as logging]
    [pallet.action :as action]
    [pallet.actions :as actions]
    [dda.config.commons.map-utils :as map-utils]))

(def Atom {(s/optional-key :plugins) [s/Str]})

(def Settings
   #{:install-aws-workaround})

(s/defn
  install-atom
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-atom")))
  (actions/packages :aptitude ["python" "gvfs-bin" "gconf2" "gconf-service"])
  (actions/remote-file
    "/tmp/atom.deb"
    :owner "root"
    :group "users"
    :mode "600"
    :url "https://atom.io/download/deb")
  (actions/exec-script ("dpkg" "-i" "/tmp/atom.deb")))

(s/defn
  install-aws-workaround
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-aws-workaround")))
  (actions/exec-checked-script
    "aws-atom-workaround"
    ("cp" "/usr/lib/x86_64-linux-gnu/libxcb.so.1" "/usr/share/atom/")
    ("sed" "-i" "'s/BIG-REQUESTS/_IG-REQUESTS/'" "/usr/share/atom/libxcb.so.1")))

(s/defn
  install-user-plugins
  [facility
   atom :- Atom]
  (let [{:keys [plugins]} atom]
    (actions/as-action
      (logging/info (str facility "-configure user: install-user-plugins")))
    (when (contains? atom :plugins)
      (doseq [plugin plugins]
        (action/with-action-options {:script-prefix :sudo}
          (actions/exec-checked-script
            (str "install-apm-plugin-" plugin)
            ("apm" "install" ~plugin)))))))

(s/defn
  install-system
  [facility :- s/Keyword
   settings
   contains-atom? :- s/Bool
   atom :- Atom]
  (let [{:keys [plugins]} atom]
    (when contains-atom?
      (install-atom facility)
      (when (contains? settings :install-aws-workaround)
         (install-aws-workaround facility)))))

(s/defn configure-user
  [facility :- s/Keyword
   os-user-name :- s/Str
   contains-atom? :- s/Bool
   atom :- Atom]
  (when contains-atom?
    (install-user-plugins facility atom)))
