## Confluentic 7.4.0 için

### Parent POM'da Repository Eklenmesi
```xml
<repositories>
    <repository>
        <id>confluent</id>
        <url>https://packages.confluent.io/maven/</url>
    </repository>
</repositories>
```

### Alt Modüllerde Dependency Eklenmesi
```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka_2.13</artifactId>
    <version>7.4.0-ccs</version>
</dependency>
<dependency>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-json-schema-serializer</artifactId>
    <version>7.4.0</version>
    <scope>compile</scope>
</dependency>
```

## KAFKA PRODUCER IMPLEMENTASYONU

### Kafka Properties Dosyası
```properties
bootstrap.servers=localhost:9092,localhost:9093,localhost:9094
key.serializer=org.apache.kafka.common.serialization.StringSerializer
value.serializer=io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer
acks=all
retries=3
linger.ms=1
batch.size=16384
compression.type=snappy
schema.registry.url=http://127.0.0.1:8081
```

### Properties Dosyasını Yükle
```java
Properties props = new Properties();
InputStream input = App.class.getClassLoader().getResourceAsStream("kafka.properties");
if (input == null) {
    throw new FileNotFoundException("kafka.properties not found in classpath");
}
props.load(input);

KafkaProducer<String, Message> kafkaProducer = new KafkaProducer<>(props);
```

### Message Sınıfı
Shared bir sınıf olması tavsiye edilir.

**`COMMON/src/main/java/com/akdag/kafka/Message.java`**
```java
public class Message {

    private String id;
    private String content;

    public Message() {}

    public Message(String id, String content) {
        this.id = id;
        this.content = content;
    }

    // Getters and Setters
}
```

### Kafka Producer ile Mesaj Gönderimi

```java
String topic = "topic";
String key = "commandKey";

Message kafkaMessage = new Message("1", "message");

ProducerRecord<String, Message> record = new ProducerRecord<>(topic, key, kafkaMessage);

kafkaProducer.send(record, (metadata, exception) -> {
   if (exception == null) {
       System.out.println(String.format(
             "Message sent successfully to topic=%s, partition=%d, offset=%d",
              metadata.topic(), metadata.partition(), metadata.offset()
       ));
   } else {
       System.err.println("Failed to send message to Kafka: " + exception.getMessage());
   }
});
```

### Key Kullanımı Hakkında
Kafka'da `key`, mesajların hangi partition'a yazılacağını belirlemek için kullanılır. Mesajların bir key ile gönderilmesi şu avantajları sağlar:

1. **Partition Belirleme**:
  - Kafka, key'in bir hash'ini alarak mesajın hangi partition'a yönlendirileceğini belirler.
  - Aynı key'e sahip mesajlar her zaman aynı partition'a yazılır, bu da belirli bir düzen sağlar.

2. **Ordering Garantisi**:
  - Aynı key ile gönderilen mesajlar, bir partition içinde sıralı olarak işlenir. Bu, özellikle verilerin sırasının önemli olduğu durumlarda faydalıdır.

3. **Partition Üzerinde Yük Dağılımı**:
  - Key kullanılarak mesajlar belirli bir mantıkla farklı partition'lara dağıtılabilir. Key kullanılmadığında Kafka, mesajları rastgele bir şekilde (round-robin) dağıtır.

4. **Örnek Kullanım**:
  - Eğer bir "kullanıcı ID" key olarak kullanılırsa, aynı kullanıcıya ait tüm mesajlar aynı partition'da bulunur ve sıralı olarak işlenir.

   ```java
   String key = "user123"; // Kullanıcı ID'si key olarak kullanılabilir.
   ```

---

## KAFKA CONSUMER IMPLEMENTASYONU

### Kafka Properties Dosyası
```properties
bootstrap.servers=localhost:9092,localhost:9093,localhost:9094
group.id=foo
key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
value.deserializer=io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer
enable.auto.commit=false
auto.offset.reset=earliest
schema.registry.url=http://127.0.0.1:8081
json.value.type=com.akdag.kafka.Message
```

### Properties Dosyasını Yükle ve Consumer Oluştur

```java
Properties properties = new Properties();
try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream("kafka.properties")) {
    if (inputStream == null) {
        throw new FileNotFoundException("Properties file 'kafka-consumer.properties' not found in resources folder");
    }
    properties.load(inputStream);
}

// Consumer'i oluştur
KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(properties);

// Topic'e abone ol
consumer.subscribe(Collections.singletonList("topic"));
```

### Dinlemek İçin Döngü Oluşturma

```java
while (true) {
    ConsumerRecords<String, Message> records = consumer.poll(Duration.ofMillis(1000));
    if (records.isEmpty()) {
        System.out.println("Nothing to read.");
    } else {
        for (ConsumerRecord<String, Message> record : records) {
            System.out.println(String.format(
                    "Key = %s, Value = %s, Topic = %s, Partition = %d, Offset = %d",
                    record.key(), record.value(), record.topic(), record.partition(), record.offset()
            ));
        }
    }
}
```

### Açıklamalar
- **`consumer.poll(Duration.ofMillis(1000))`**:
  - Kafka'dan mesajları çeker. Eğer belirlenen sürede mesaj yoksa boş bir sonuç döner.

- **`records.isEmpty()`**:
  - Gelen mesajların boş olup olmadığını kontrol eder.

- **`consumer.subscribe(Collections.singletonList("topic"))`**:
  - Belirtilen topic'e abone olur. Consumer, bu topic'teki mesajları dinler.

---
