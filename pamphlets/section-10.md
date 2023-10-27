# Section 10 - Kafka Real World Project

https://codepen.io/Krinkle/pen/BwEKgW?editors=1010

https://esjewett.github.io/wm-eventsource-demo/

## 55 - Real World Project Overview
### Project goal: Wikimedia stream to opensearch
We're gonna get data from wikimedia as a stream through a kafka producer into kafka topics and then we're gonna create a kafka consumer
that will take this data and send it to opensearch(open source fork of elastic search).

![](./img/55-55-1.png)

![](./img/55-55-2.png)

### 55 - Wikimedia Recent Change Stream
https://stream.wikimedia.org/v2/stream/recentchange

https://www.conduktor.io/kafka-for-beginners