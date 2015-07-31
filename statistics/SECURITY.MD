#Securing your Elasticsearch and Kibana

Currently there are two plugins which are not DIY solutions for securing elasticsearch
- [Elasticsearch Shield (commercial product)](https://www.elastic.co/products/shield)
- [Search guard (open source)](https://github.com/floragunncom/search-guard)

With an reverse-proxy and a couple easy settings we get a fairly basic secure setup.

Elasticsearch is basically HTTP-based and it publishs its REST API on the port range [9200-9300 by default](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-http.html).
For between node-to-node transport communication it uses the [9300-9400 port range](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-transport.html).
It is not recommended and necesarry to let these ports open for the outside world but required for the internal network components (52° North applications and Elasticsearch cluster nodes) to be accessible.

The Kibana 4 server can only be run with the pre-installed node.js server and can't be deployed on [third-party servers](https://github.com/elastic/kibana/issues/1628#issuecomment-58657308). By defaults it listens on the port 5601 and try to connect to the localhost:9200 port of the running Elasticsearch service. It is your decision to whom do you let access of your 5601 Kibana interface port. Currently no open-source (Shield is an commercial product) solution exists to authenticate/authorize Kibana/Elasticsearch users. 

Kibana 4.2 probably will have a lightweight [authentication mechanism](https://www.elastic.co/blog/kurrently-kibana-2015-03-26).

If you would like to let the outside world query your elasticsearch but you would like to have some constraints consider this [blog post](https://www.elastic.co/blog/playing-http-tricks-nginx) for starters.

###Enabling scripting
"*We recommend running Elasticsearch behind an application or proxy, which protects Elasticsearch from the outside world. If users are allowed to run inline scripts (even in a search request) or indexed scripts, then they have the same access to your box as the user that Elasticsearch is running as. For this reason dynamic scripting is allowed only for sandboxed languages by default.*

*First, you should not run Elasticsearch as the root user, as this would allow a script to access or do anything on your server, without limitations. Second, you should not expose Elasticsearch directly to users, but instead have a proxy application inbetween. If you do intend to expose Elasticsearch directly to your users, then you have to decide whether you trust them enough to run scripts on your box or not.*"

[More about scripting](https://www.elastic.co/guide/en/elasticsearch/reference/master/modules-scripting.html)
