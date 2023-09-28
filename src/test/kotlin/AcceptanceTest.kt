import com.wallapop.dataproduct.DataProductConfig
import com.wallapop.dataproduct.consumer.DataProductConsumer
import com.wallapop.dataproduct.consumer.DataProductSubscriber
import com.wallapop.dataproduct.library.serde.DataProductRecordSerde
import com.wallapop.dataproduct.products.KnownDataProducts
import com.wallapop.dataproduct.products.category.CategoryAvro
import com.wallapop.dataproduct.products.category.CategoryDataProduct
import java.time.Instant
import java.util.Properties
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.awaitility.Durations.ONE_MINUTE
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.junit.jupiter.api.Test

class AcceptanceTest {

    @Test
    fun name() {
        App.start()
        val config = KnownDataProducts[CategoryDataProduct::class.java]!!
        val serde = config.serde() as DataProductRecordSerde<CategoryAvro>
        val data = CategoryAvro("le-id-la-la-trucutru", null, "le-name", emptyList(), true, Instant.now().toEpochMilli())

        val producer = KafkaProducer<String, ByteArray>(Properties().also {
            it[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
            it[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
            it[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java.name
        })
        producer.send(ProducerRecord(config.sinkTopic, serde.serialize(data)))

        await atMost ONE_MINUTE untilAsserted {
            assert(data.name == InMemoryDatabase.last())
        }
    }
}


class LeSubscriber : DataProductSubscriber<CategoryDataProduct> {
    override fun process(records: List<CategoryDataProduct>) {
        records.forEach {
            println(it)
            InMemoryDatabase.add(it.name)
        }
    }

    override fun dataProduct() = CategoryDataProduct::class.java

    override fun name() = "testing-because-its-good-to-do-so"
}

object InMemoryDatabase {
    private val records = mutableListOf<String>()

    fun add(record: String) {
        records.add(record)
    }
    fun reset() {
        records.removeAll { true }
    }

    fun last() = records.lastOrNull()
}

object App {
    fun start() {
        val testConfig = DataProductConfig(
            bootstrapServers = "localhost:9092",
            clientId = "gerard-camps",
            consumerGroupPrefix = "testing",
            authenticationProtocol = "PLAINTEXT",
        )
        val consumer = DataProductConsumer(testConfig)
        val subscriber = LeSubscriber()
        consumer.register(subscriber)
        consumer.start()
    }
}
