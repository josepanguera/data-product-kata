import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.Properties

/**
 * # Notes
 *
 *  You need to be connected to the beta VPN and have valid AWS credentials for the beta account
 *
 * See http://kafka-admin.wallapop.beta/ui/clusters/beta/all-topics/domain-events
 */

fun main() {
    val conf = Properties().also {
        it[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        it[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = "SASL_SSL"
        it[SaslConfigs.SASL_MECHANISM] = "AWS_MSK_IAM"
        it[SaslConfigs.SASL_JAAS_CONFIG] = "software.amazon.msk.auth.iam.IAMLoginModule required;"
        it[SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS] = "software.amazon.msk.auth.iam.IAMClientCallbackHandler"
        it[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        it[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        it[ConsumerConfig.GROUP_ID_CONFIG] = TODO() // Change it to something unique to prevent sharing consumer group with other people
        it[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        it[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 10
    }
    val consumer = KafkaConsumer<String, String>(conf)

    consumer.subscribe(listOf("domain-events"))

    (0..10).forEach {
        println("\n\nIteration $it")
        val records = consumer.poll(Duration.ofSeconds(10))
        records.forEach { record -> println("\tRecord: $record}") }

        Thread.sleep(1000)
    }

    consumer.close()
}
