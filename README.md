# Maps Coffee

proxy server for application CoffeeMap

## Routing 
```
/               - some leaflat app (it might be web-version)
/api            - just info message (need to add info about API using)
/api/points     - retrieving points directly via overpass API
/api/v2/points  - retrieving points from elastic index (API the same as /api/points)
```
  GET args:
  * n - north bound of user screen
  * s - south bound of user screen
  * e - east bound of user screen
  * w - west bound of user screen

```
  (n,e)
  +-------------------------+
  |                         |
  |                         |
  |                         |
  |                         |
  |                         |
  |                         |
  |                         |
  |                         |
  |                         |
  |                         |
  +-------------------------+
                        (s,w)
```

## What does server do?

## v1
* takes GET args
* composes XML document according to overpas API
* makes request to overpass API
* parses responce
* composes new JSON document
* returns it to requester

#### Overpass API body:

```
<bbox-query s="$n" n="$s" w="$w" e="$e"/>     // bbox 
<query type="node">
<item/>
<has-kv k="amenity" v="cafe"/>                // filter by tags (amenity & cuisine)
<has-kv k="cuisine" v="coffee_shop"/>
</query>
<print/>
```

### v2
* takes GET args
* composes JSON document according to ElasticSearch DSL
* makes request to ElasticSearch service               
* parses responce
* composes new JSON document
* returns it to requester

#### Elastic request body:
```
{
 "size" : 1000,                          // number of documents (points)
 "query":{
    "bool" : {
        "must" : {
            "match_all" : {}             // search query (empty match all documents)
         },
        "filter" : {
            "geo_bounding_box" : {       // bbox (s,w) -> (n,e)
                "location" : {
                    "top_left" : {
                        "lat" : $s,
                        "lon" : $w
                    },
                    "bottom_right" : {
                        "lat" : $n,
                        "lon" : $e
                    }
                }
            }
        }
    }
  }
}
```

```
./scripts/fill_index.pl - elastic index filler script 
```


