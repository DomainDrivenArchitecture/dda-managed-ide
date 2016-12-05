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
(ns org.domaindrivenarchitecture.pallet.servertest.test.packages
  (:require
    [schema.core :as s]
    [org.domaindrivenarchitecture.pallet.servertest.tests :as tests]
    [org.domaindrivenarchitecture.pallet.servertest.fact.packages :as packages-res]
    [org.domaindrivenarchitecture.pallet.servertest.scripts.core :refer :all]))

(defn parse-packages
  [packages-resource]
  (map #(zipmap [:state :package :version :arch :desc]
              (clojure.string/split % #"\s+|/"))
     (rest (rest (rest (rest (rest packages-resource))))))
  )

(defn filter-installed-package
  "filter for installed packages."
  [package parsed-package-line]
  (= (:package parsed-package-line) package)
  )

(s/defn installed? :- s/Bool
  [package :- s/Str 
   packages-resource]
  (some? (filter 
           #(filter-installed-package package %)
           (parse-packages packages-resource)))
  )

(s/defn test-installed? :- s/Bool
  [package :- s/Str]
  (tests/testclj-resource 
    packages-res/res-id-packages
    (partial installed? package))
  )