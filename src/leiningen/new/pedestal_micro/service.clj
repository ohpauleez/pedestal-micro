(ns {{namespace}}
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [ns-tracker.core :refer [ns-tracker]]
            [environ.core :refer [env]]
            [{{namespace}}.routes :refer [routes]]
            [{{namespace}}.db :as db])
  (:gen-class))

(defonce modified-namespaces
  (if (env :prod)
    (constantly nil)
    (ns-tracker ["src"])))

(def service
  {::http/host (or (env :host) "0.0.0.0")
   ::http/port (Integer/parseInt (or (env :port) "8080"))
   ::http/type :jetty
   ::http/join? false
   ::http/resource-path "/public"
   ::http/routes (fn []
                   (doseq [ns-sym (modified-namespaces)]
                     (require ns-sym :reload))
                   routes)})

(defn server [service-overrides]
  (http/create-server (merge service
                             service-overrides)))

(defn start [& args]
  (db/bootstrap! db/uri)
  (let [service-overrides (apply hash-map args)
        server (server service-overrides)]
    (http/start server)
    server))

(defn stop [serv]
  (http/stop serv)
  serv)

(defn restart [serv]
  (http/start (stop serv))
  serv)

(defn -main [& args]
  (start ::http/join? true
         ::http/routes routes))
