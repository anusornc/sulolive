(ns eponai.server.email
  (:require [eponai.server.http :as h]
            [postal.core :as email]
            [datomic.api :as d]
            [environ.core :refer [env]]
            [eponai.server.datomic.pull :as p]))

(defn- smtp []
  {:host (env :smtp-host)
   :user (env :smtp-user)
   :pass (env :smtp-pass)
   :tls  (env :smtp-tls)
   :port (env :smtp-port)})

(defn- send-email
  "Send a verification email to the provided address with the given uuid.
  Will send a link with the path /verify/:uuid that will verify when the user opens the link."
  [smtp address uuid]
  (let [link (str "http://localhost:3000/verify/" uuid)
        body {:from    "info@gmail.com"
              :to      "dianagren@gmail.com"
              :subject "Hi there, this is your confirmation email."
              :body    (str "Email: " address ". Click this link: " link)}
        status (email/send-message smtp body)]

    (println "Sent verification email to uuid: " uuid)
    (println "Email sent status: " status)
    (if (= 0 (:code status))
      status
      (throw (ex-info (:message status) {:cause   ::email-error
                                         :status  ::h/service-unavailable
                                         :message (:message status)
                                         :data    {:email address
                                                   :uuid  uuid
                                                   :error (:error status)}})))))
(defn send-email-fn
  "Function that checks if there's any pending verification for the provided user emal,
  and sends a verification email if so.

  Conn is the connection to the database."
  [conn]
  (fn [email]
    (let [db (d/db conn)
          user (p/user db email)
          pending-verifications (p/verifications db user :user/email :verification.status/pending)]

      (cond (first pending-verifications)
            (send-email (smtp)
                        email
                        (->> pending-verifications
                             first
                             :verification/uuid))))))
