import com.amazonaws.services.schemaregistry.kafkastreams.GlueSchemaRegistryKafkaStreamsSerde
import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants
import com.wallapop.dataproduct.library.avro.AvroSchema
import org.apache.avro.generic.GenericData
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Duration
import java.util.*

/**
 * # NOTES
 * Explain what is avro
 * show Glue console
 * explain SCHEMA_AUTO_REGISTRATION_SETTING
 * show KAFKA UI with garbage
 * https://eu-west-1.console.aws.amazon.com/glue/home?region=eu-west-1#/v2/data-catalog/schemas/view/realtime-data-product/sandwiches/1
 */

data class Sandwich(val id: String, val name: String) : GenericData.Record(AvroSchema.fromResource("/avro/sandwich.avsc")) {
    override fun get(i: Int): Any? = listOf(
        id,
        name,
    )[i]
}

val serde = GlueSchemaRegistryKafkaStreamsSerde().apply {
    configure(
        mapOf(
            AWSSchemaRegistryConstants.AWS_REGION to "eu-west-1",
            AWSSchemaRegistryConstants.AVRO_RECORD_TYPE to "GENERIC_RECORD",
            AWSSchemaRegistryConstants.REGISTRY_NAME to "realtime-data-product",
            AWSSchemaRegistryConstants.SCHEMA_NAME to "sandwiches",
            AWSSchemaRegistryConstants.COMPRESSION_TYPE to "ZLIB",
            AWSSchemaRegistryConstants.DATA_FORMAT to "AVRO",
            AWSSchemaRegistryConstants.SCHEMA_AUTO_REGISTRATION_SETTING to false,
        ),
        false,
    )
}

fun main() {
    produce()
    consume()
}

fun produce() {
    val conf = Properties().also {
        it[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        it[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        it[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java.name
    }
    val producer = KafkaProducer<String, ByteArray>(conf)
    val sandwich1 = Sandwich("jamon", "queso")
    val sandwich2 = Sandwich("chorizo", "tomate")

    listOf(
        ProducerRecord("sandwiches-binary", "foo", serde.serializer().serialize(null, sandwich1)),
        ProducerRecord("sandwiches-binary", "key", serde.serializer().serialize(null, sandwich2)),
    ).forEach {
        producer.send(it)
    }
    producer.flush()
    producer.close()
}

fun consume() {
    val conf = Properties().also {
        it[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        it[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        it[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = ByteArrayDeserializer::class.java.name
        it[ConsumerConfig.GROUP_ID_CONFIG] = "el-zorrete-capullete"
        it[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        it[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 1
    }
    val consumer = KafkaConsumer<String, ByteArray>(conf)

    consumer.subscribe(listOf("sandwiches-binary"))

    (0..2).forEach {
        println("\n\nIteration $it")
        val records = consumer.poll(Duration.ofSeconds(1))
        records.forEach { record -> println("\tRecord: ${serde.deserializer().deserialize("", record.value())}") }

        Thread.sleep(1000)
    }

    consumer.close()
}
