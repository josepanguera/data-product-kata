import java.util.Properties
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

/**
 * # NOTES
 *
 * - Explain topics, serializers, that kafka knows nothing about the contents and format of the data it has.
 * - Show Kafka UI.
 */

fun main() {
    val conf = Properties().also {
        it[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        it[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        it[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
    }
    val producer = KafkaProducer<String, String>(conf)

    listOf(
        ProducerRecord("sandwiches", "foo", "bar"),
        ProducerRecord("sandwiches", "key", "value"),
    ).forEach {
        producer.send(it)
    }
    producer.flush()
    producer.close()
}
