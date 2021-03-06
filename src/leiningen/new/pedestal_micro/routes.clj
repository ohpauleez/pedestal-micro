(ns {{namespace}}.routes
  (:require [io.pedestal.http.route.definition :refer [defroutes]]
            [datomic.api :as d]
            [ring.util.response :as ring-resp]
            [{{namespace}}.interceptor :as interceptor]
            [{{namespace}}.bootstrap :refer [conf]]))

(defn hello-world
  [request]
  (ring-resp/response "Hello, World!"))

(defroutes routes
  [[["/" {:get hello-world}
     ^:interceptors [(interceptor/insert-datomic (conf :datomic-uri))]]]])
