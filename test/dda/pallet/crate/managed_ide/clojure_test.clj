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


(ns dda.pallet.crate.managed-ide.clojure-test
  (:require
    [clojure.test :refer :all]
    [schema.core :as s]
    [dda.pallet.crate.managed-ide.clojure :as sut]))


(deftest lein-user-profile-test
  (testing
    (is (= {:user
            {:plugins
             [['lein-release "1.0.5"]
              ['slamhound "1.5.5"]
              ['lein-cloverage "1.0.6"]
              ['jonase/eastwood "0.2.3"]
              ['lein-kibit "0.1.2"]
              ['lein-ancient "0.6.10"]
              ['nightlight/lein-nightlight "1.6.1"]]
             :dependencies
             [['pjstadig/humane-test-output "0.7.1"]]
             :injections
             ['(require 'pjstadig.humane-test-output)
              '(pjstadig.humane-test-output/activate!)]}}
           (sut/lein-user-profile {:os-user-name (name :test)
                                   :settings #{:install-nightlight}})))
    (is (= {:user
            {:plugins
             [['lein-release "1.0.5"]
              ['slamhound "1.5.5"]
              ['lein-cloverage "1.0.6"]
              ['jonase/eastwood "0.2.3"]
              ['lein-kibit "0.1.2"]
              ['lein-ancient "0.6.10"]]
             :dependencies
             [['pjstadig/humane-test-output "0.7.1"]],
             :injections
             ['(require 'pjstadig.humane-test-output)
              '(pjstadig.humane-test-output/activate!)]}}
           (sut/lein-user-profile {:os-user-name (name :test)})))
    ))
