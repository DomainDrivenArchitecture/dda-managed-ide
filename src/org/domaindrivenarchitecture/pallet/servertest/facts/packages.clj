(ns org.domaindrivenarchitecture.pallet.servertest.facts.packages
  (:require
    [org.domaindrivenarchitecture.pallet.servertest.facts :refer :all]
    [org.domaindrivenarchitecture.pallet.servertest.tests :refer :all]
    [pallet.stevedore :refer :all]
    [pallet.script :as script]
    [pallet.script.lib :refer :all]))

(def res-id-packages ::packages)
(defn define-resources-packages
  "Defines the packages installed."
  []
  (define-resource-from-script res-id-packages "dpkg --get-selections | grep deinstall"))