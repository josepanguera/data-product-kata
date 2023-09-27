import com.wallapop.dataproduct.DataProductConfig
import com.wallapop.dataproduct.consumer.DataProductConsumer
import com.wallapop.dataproduct.consumer.DataProductConsumerProperties
import com.wallapop.dataproduct.consumer.DataProductSubscriber
import com.wallapop.dataproduct.products.category.CategoryDataProduct
import java.time.Duration

/**
 * # Notes
 *
 *  You need to be connected to the beta VPN and have valid AWS credentials for the beta account
 *
 * Docs: https://github.com/Wallapop/realtime-data-product/blob/master/data-product-library/docs/consumption-guide.md
 * See http://kafka-admin.wallapop.beta/ui/clusters/beta/all-topics/category-events
 * Explain default config
 * Explain KnownDataProducts and register
 */

// TODO: Acceptance tests


fun main() {
    val config = DataProductConfig(
        consumerGroupPrefix = TODO(), // Change it to something unique to prevent sharing consumer group with other people
        clientId = "task-id", // Identifier. If you change this value you'll see it on the Kafka admin
        bootstrapServers = BOOTSTRAP_SERVERS
    )
    // val consumer = DataProductConsumer(config) // With defaults
    val consumer = DataProductConsumer(config) {
        consumerProperties = DataProductConsumerProperties(
            pollTimeout = Duration.ofSeconds(5)
        )
    }
    consumer.register(MySubscriber())
//    # Override defaults for this particular subscriber
//    consumer.register(MySubscriber()) {
//        consumerProperties = DataProductConsumerProperties(maxBatchRecords = 2)
//    }
    consumer.start()  // Starts all registered subscribers
}

class MySubscriber : DataProductSubscriber<CategoryDataProduct> {
    override fun process(records: List<CategoryDataProduct>) {
        records.forEach { println(it) }
    }

    override fun dataProduct() = CategoryDataProduct::class.java

    override fun name() = "my-projection" // Identifier. If you change this value you'll see it on the Kafka admin
}
