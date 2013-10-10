(defproject list-events-clojure "0.1.0-SNAPSHOT"
  :description "Small example on how to access caldav using clojure and caldva4j"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 ; Pls download and mvn install it yourself 
                 ; https://code.google.com/p/caldav4j/source/checkout
                 [org.osaf/caldav4j "0.8-SNAPSHOT"]]
  :main list-events-clojure.core)
