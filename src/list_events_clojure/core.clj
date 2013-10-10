(ns list-events-clojure.core
  (:import (net.fortuna.ical4j.model Component)
           (org.apache.commons.httpclient.auth AuthScope)
           (org.osaf.caldav4j CalDAVCollection CalDAVConstants)
           (org.osaf.caldav4j.methods CalDAV4JMethodFactory HttpClient)
           (org.osaf.caldav4j.util GenerateQuery)
           (org.apache.commons.httpclient UsernamePasswordCredentials)))

(def configuration {
                    :username "username"
                    :password "secret"
                    :host     "hostname"
                    :port     443
                    :proto    "https"
                    ; Default user calendar in zimbra
                    :collection-path "/dav/username/Calendar/"
                    })

(defn- initialize-http-client []
  (let [http-client (HttpClient.)
        host-config (.getHostConfiguration http-client)
        http-credentials (UsernamePasswordCredentials. (:username configuration), (:password configuration))
        ]
    (.setHost host-config (:host configuration) (:port configuration) (:proto configuration))
    (-> http-client (.getState) (.setCredentials AuthScope/ANY http-credentials))
    (-> http-client (.getParams) (.setAuthenticationPreemptive true))
    ; If you like a proxy, do s.th. like that
    ;	httpClient.getHostConfiguration().setProxy(credential.getProxyHost(), (credential.getProxyPort() > 0) ? credential.getProxyPort() : 8080);
    http-client
    ))

(defn- create-query []
  (let [gq (GenerateQuery.)]
    ; Date Format yyyyMMdd
    (.setFilter gq "VEVENT [20131001T000000Z;20131010T000000Z] : STATUS!=CANCELLED")
    (.generate gq)))

(defn- seconds [event-date]
  (.getTime (.getDate event-date)))

(defn- event-duration [event]
  (let [start-seconds (seconds (.getStartDate event))
        end-seconds   (seconds (.getEndDate event))]
    (-> end-seconds (- start-seconds) (/ (* 1000. 60. 60.)))))

(defn -main []
  (let [http-client (initialize-http-client)
        collection-path (:collection-path configuration)
        host-configuration (.clone (.getHostConfiguration http-client))
        method-factory (CalDAV4JMethodFactory.)
        collection (CalDAVCollection. collection-path host-configuration method-factory CalDAVConstants/PROC_ID_DEFAULT)
        calendars (.queryCalendars collection http-client (create-query))
        ]
    (doseq [cal calendars
            event (-> cal (.getComponents) (.getComponents Component/VEVENT))]
      (println "Event: " event)
      (println "Duration " (format "%.2f" (event-duration event)))
      (println "\n\n"))))

