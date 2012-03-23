(ns blog.views
  (:use hiccup.core
        blog.config
        [blog.css :only [acolor]]))

(defn make-post-name [post]
  (str *posts-out-folder* (post :title) ".html"))

(defn make-tag-name [tag]
  (str *tags-out-folder* (tag :name) ".html"))

(defhtml sidebar-tag-link [tag]
  [:a.sidebar-item {:href (make-tag-name tag)} (tag :name)]
  [:div.tag-number " (" (tag :n) ")"]
  [:br])

(defn sidebar-item [text link & tags]
  (case link
    :tags (map sidebar-tag-link (first tags))
    :title [:div.sidebar-title [:br] text]
    (html [:a.sidebar-item {:href link} text])))

(defn def-sidebar [tags]
  (def sidebar
    [:div#sidebar
     [:div#logo
     [:a#logo-link {:href *out-html-path*}
      [:img {:src *logo-path*}]]]
     [:div#links-box
      (map #(apply (fn [x y] (sidebar-item x y  tags)) %)
           (partition 2 *sidebar-items*))]
     [:div#about-box
      *about-text*]]))

(def footer
  [:div#footer
   "Site generated by "
   [:a.footer-link {:href "http://clojure.org/"}
    "Clojure"]
   " and "
   [:a.footer-link {:href "http://github.com/weavejester/hiccup"} "Hiccup"]])
(defhtml tag-link [name]
  [:a.tag-link {:href (str *out-folder* *tags-folder* name ".html")} name])

(defhtml html-post [post]
  [:div.post
   [:a.post-header {:href (make-post-name post)}
    (post :title)]
   [:hr.post-header-hr]
   [:p.post-body (post :body)]
   [:div.post-info
    (str (post :date) "<div id=\"info-sep\"> | </div>"
     (apply (partial str "Tags: ")
                (interpose ", " (map tag-link (post :tags)))))]
   [:hr.between-post-hr]])

(defhtml page-prelude [& body]
  [:link {:rel "stylesheet"
          :type "text/css"
          :href *out-css-path*}]
  [:link {:rel "shortcut icon"
          :type "image/x-icon"
          :href *favicon*}]
  [:title *title*]
  [:body
   [:div#content
    body
    sidebar
    footer]])

(defhtml home-page [posts]
  ;; posts is a vector of maps {:title "" :tags
  ;; [""] :body ""}
  (page-prelude
   [:div#posts
    (map html-post posts)]))

(defhtml post-page [post]
  (page-prelude
   [:div#posts
    (html-post post)]))

(defhtml tag-page [tag]
  ;; tag is {:tag "name" :n #of articles
  ;; :posts [posts]}
  (page-prelude
   [:div#tag-header (str (tag :n) " Article(s) filed under: " (tag :name))]
   [:div#posts
    (map html-post (tag :posts))]))