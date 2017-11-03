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

(ns dda.pallet.dda-managed-ide.infra.clojure
  (:require
    [schema.core :as s]
    [pallet.actions :as actions]
<<<<<<< HEAD:src/dda/pallet/dda_managed_ide/infra/clojure.clj
    [dda.pallet.crate.util :as util]))
=======
    [dda.config.commons.map-utils :as map-utils]
    [org.domaindrivenarchitecture.pallet.crate.util :as util]))
>>>>>>> issue_26:src/dda/pallet/crate/managed_ide/clojure.clj

(def Auth
  {:username s/Str
   :password s/Str})

(def LeiningenUserProfileConfig
  {:os-user-name s/Str
   (s/optional-key :signing-gpg-key) s/Str
   (s/optional-key :auth-clojars) Auth
   (s/optional-key :settings) (hash-set (s/enum :install-nightlight))})

(s/defn lein-user-profile
  "generates a valid lein profile config."
  [lein-config :- LeiningenUserProfileConfig]
  (let [settings (-> lein-config :settings)]
    (merge
      {:user
       {:plugins
        (into
          [['lein-release "1.0.5"]
           ['slamhound "1.5.5"]
           ['lein-cloverage "1.0.6"]
           ['jonase/eastwood "0.2.3"]
           ['lein-kibit "0.1.2"]
           ['lein-ancient "0.6.10"]]
          (if (contains? settings :install-nightlight)
            [['nightlight/lein-nightlight "1.6.1"]]
            []))
        :dependencies [['pjstadig/humane-test-output "0.7.1"]]
        :injections ['(require 'pjstadig.humane-test-output)
                     '(pjstadig.humane-test-output/activate!)]}}
      (if (contains? lein-config :signing-gpg-key)
        {:user
         {:signing {:gpg-key (get-in lein-config [:signing-gpg-key])}}}
        {})
      (if (contains? lein-config :auth-clojars)
        {:auth
         {:repository-auth
          {#"clojars"
           (get-in lein-config [:auth-clojars])}}}
        {}))))


(defn install-leiningen
  []
  "get and install lein at /opt/leiningen"
  (actions/directory
    "/opt/leiningen"
    :owner "root"
    :group "users"
    :mode "755")
  (actions/remote-file
    "/opt/leiningen/lein"
    :owner "root"
    :group "users"
    :mode "755"
    :url "https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein")
  (actions/remote-file
    "/etc/profile.d/lein.sh"
    :literal true
    :content
    (util/create-file-content
      ["PATH=$PATH:/opt/leiningen"
       "export PATH"])))


(s/defn configure-user-leiningen
  "configure lein settings"
  [lein-config :- LeiningenUserProfileConfig]
  (let [os-user-name (get-in lein-config [:os-user-name])
        path (str "/home/" os-user-name "/.lein/")]
   (actions/directory
     path
     :owner os-user-name
     :group os-user-name
     :mode "755")
   (actions/remote-file
     (str path "profiles.clj")
     :owner os-user-name
     :group os-user-name
     :literal true
     :content
     (str (lein-user-profile lein-config)))))
