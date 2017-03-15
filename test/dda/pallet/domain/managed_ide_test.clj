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


(ns dda.pallet.domain.managed-ide-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [dda.pallet.domain.managed-ide.repos :as domain-repos]
    [dda.pallet.domain.managed-ide :as sut]))

(def domain-input
  {:ide-user :test
   :vm-platform :aws
   :dev-platform :clojure-nightlight})

(def expected-ide-configuration
  {:ide-user :test
   :project-config domain-repos/dda-projects
   :clojure {:os-user-name "test"
             :settings #{:install-nightlight}}
   } 
)

(def expected-configuration
  {:dda-managed-ide expected-ide-configuration
   :dda-managed-vm
   {:vm-user :test
    :settings #{:install-open-jdk-8
                :install-git
                :install-linus-basics}}
   })

(deftest test-whole-domain
  (testing
    "test the ide schema"
    (is 
      (=
        expected-configuration
        (select-keys
          (sut/ide-convention domain-input)
          [:dda-managed-ide :dda-managed-vm]))
    ))
  )

(deftest test-ide-domain
  (testing
    "test the ide schema"
    (is 
      (=
        expected-ide-configuration
        (sut/default-ide-config :test :clojure-nightlight :aws))
    ))
  )

