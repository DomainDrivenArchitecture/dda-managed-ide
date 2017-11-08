; Licensed to the Apache Software Foundation (ASF) under one
; or more contributor license agreements. See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership. The ASF licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License. You may obtain a copy of the License at
; http://www.apache.org/licenses/LICENSE-2.0
;
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
(ns dda.pallet.dda-managed-ide.app.external-config
  (:require
    [schema.core :as s]
    [clojure.string :as str]
    [clojure.edn :as edn]
    [keypin.core :refer [defkey letval] :as k]
    [keypin.util :as ku]
    [dda.config.commons.user-env :as user-env]))

(def ExternalConfigSchema
  {:provisioning {:ip s/Str
                  :login s/Str
                  :password s/Str}
   :user {:name s/Str
          :password s/Str
          :email s/Str
          (s/optional-key :ssh) {:ssh-pub-key s/Str
                                 :ssh-prvate-key s/Str}
          (s/optional-key :gpg) {:gpg-public-key s/Str
                                 :gpg-private-key s/Str
                                 :gpg-passphrase s/Str}}})

(defn dispatch-file-type
  "Dispatches a string to a keyword which represents the file type."
  [file-name]
  (keyword (last (str/split file-name #"\."))))

(defmulti parse-config dispatch-file-type)
(defmethod parse-config :edn
  [file-path]
  (ku/clojurize-data (k/read-config [file-path])))

(defn ex-config
  "reads external edn-config"
  [user-config]
  (parse-config user-config))

(def user-config-path
  "user-config.edn")

(defn provisioning-ip []
  (let [file user-config-path]
   (-> (ex-config file) :provisioning :ip)))

(defn provisioning-user []
  (let [file user-config-path]
   {:login (-> (ex-config file) :provisioning :login)
    :password (-> (ex-config file) :provisioning :password)}))

(def ssh-pub-key
  (user-env/read-ssh-pub-key-to-config))

(defn authorized-keys [user-edn]
   (let [{:keys [ssh]} user-edn]
    (if (contains? user-edn :ssh)
      {:authorized-keys [(user-env/string-to-pub-key-config (:ssh-pub-key ssh))]}
      {})))

(defn gpg [user-edn]
   (let [{:keys [gpg]} user-edn]
    (if (contains? user-edn :gpg)
      {:gpg {:trusted-key {:public-key (:gpg-public-key gpg)
                           :private-key (:gpg-private-key gpg)
                           :passphrase (:gpg-passphrase gpg)}}}
      {})))

(defn user-config []
   (let [{:keys [user]} (ex-config user-config-path)]
    {(keyword (:name user))
     (merge
       {:clear-password (:password user)}
       (authorized-keys user)
       (gpg user))}))

(defn ide-config []
  (let [file user-config-path]
   {:ide-user (keyword (-> (ex-config file) :user :name))
    :vm-platform :virtualbox
    :dev-platform (keyword (-> (ex-config file) :dev-platform))}))
