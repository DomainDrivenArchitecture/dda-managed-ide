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
    [pallet.actions :as actions]))


(defn install [config]
  (let [atom-config (-> config :atom)
        settings (-> atom-config :settings)]
    (actions/package "python")
    (actions/package "gvfs-bin")
    (actions/remote-file
      "/tmp/atom.deb"
      :owner "root"
      :group "users"
      :mode "600"
      :url "https://atom.io/download/deb")
    (actions/exec-script ("dpkg" "-i" "/tmp/atom.deb"))
    (when (contains? settings :install-aws-workaround)
      (actions/exec-checked-script
        "aws-atom-workaround"
        ("cp" "/usr/lib/x86_64-linux-gnu/libxcb.so.1" "/usr/share/atom/")
        ("sed" "-i" "'s/BIG-REQUESTS/_IG-REQUESTS/'" "/usr/share/atom/libxcb.so.1")))))




(defn install-user-plugins [config]
  (let [atom-config (-> config :atom)]
    (when (contains? atom-config :plugins)
      (let [plugins (-> atom-config :plugins)]
        (doseq [plugin plugins]
          (actions/exec-checked-script
            (str "install-apm-plugin-" plugin)
            ("apm install" ~plugin)))))))
