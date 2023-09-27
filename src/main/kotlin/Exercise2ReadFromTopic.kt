import java.time.Duration
import java.util.Properties
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer

/**
 * NOTES:
 *
 * Group ID, consumer groups and `subscribe`
 * Offsets and commits (too advanced?). Auto-commit.
 * Poll timeout / duration / next retry
 * AUTO_OFFSET_RESET_CONFIG
 * MAX_POLL_RECORDS_CONFIG
 * show in kafka UI how to reset an offset
 */

fun main() {
    val conf = Properties().also {
        it[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        it[ConsumerConfig.GROUP_ID_CONFIG] = "el-zorro-melancolico"
        it[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        it[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 1
        it[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        it[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
    }
    val consumer = KafkaConsumer<String, String>(conf)

    consumer.subscribe(listOf("sandwiches"))

    (0..2).forEach {
        println("\n\nIteration $it")
        val records = consumer.poll(Duration.ofSeconds(1))
        records.forEach { record -> println("\tRecord: $record") }

        Thread.sleep(1000)
    }

    consumer.close()
}
