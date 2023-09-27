import com.wallapop.dataproduct.DataProductConfig
import com.wallapop.dataproduct.consumer.DataProductConsumer
import com.wallapop.dataproduct.consumer.DataProductConsumerProperties
import com.wallapop.dataproduct.consumer.DataProductSubscriber
import com.wallapop.dataproduct.products.category.CategoryDataProduct
import java.time.Duration

/**
 * Docs: https://github.com/Wallapop/realtime-data-product/blob/master/data-product-library/docs/consumption-guide.md
 * Explain default config
 */

// TODO: Acceptance tests
// TODO: Do we use a real data product or do we register a new one?


fun main() {
    val config = DataProductConfig(
        consumerGroupPrefix = TODO(),
        clientId = "task-id",
        bootstrapServers = "b-2.dataproduct.p6318s.c1.kafka.eu-west-1.amazonaws.com:9098,b-3.dataproduct.p6318s.c1.kafka.eu-west-1.amazonaws.com:9098,b-1.dataproduct.p6318s.c1.kafka.eu-west-1.amazonaws.com:9098",
    )
    val consumer = DataProductConsumer(config) {
        consumerProperties = DataProductConsumerProperties(
            pollTimeout = Duration.ofSeconds(5)
        )
    }
    consumer.register(MySubscriber())
    consumer.start()
}

class MySubscriber : DataProductSubscriber<CategoryDataProduct> {
    override fun process(records: List<CategoryDataProduct>) {
        records.forEach { println(it) }
    }

    override fun dataProduct() = CategoryDataProduct::class.java

    override fun name() = "my-projection"
}
