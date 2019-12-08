package uk.co.empyrean.hazelcast

import com.github.javafaker.Faker
import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import java.io.Serializable
import java.time.LocalDate
import java.util.UUID


fun main() {
    val clientConfig = ClientConfig()
    clientConfig.groupConfig.name = "dev"
    clientConfig.networkConfig.addAddress("192.168.99.1:5701")

    val hazelcast = HazelcastClient.newHazelcastClient(clientConfig)

    val personMap = hazelcast.getMap<UUID, Person>("person-map")
    createCustomers().forEach {
        personMap[it.id] = it
        println(personMap[it.id])
    }

    createEmployees().forEach {
        personMap[it.id] = it
        println(personMap[it.id])
    }

    println(personMap.size)

    hazelcast.shutdown()
}

interface Person : Serializable {
    val name: String
    val email: String
    val id: UUID get() = UUID.randomUUID()
}

data class Customer(
    override val name: String,
    override val email: String,
    override val id: UUID
) : Person

data class Employee(
    override val name: String,
    override val email: String,
    val doj: LocalDate,
    override val id: UUID = UUID.randomUUID()
) : Person

fun createCustomers(): Sequence<Customer> {
    val faker = Faker()

    return Array(5) {
        val id: UUID = UUID.randomUUID()
        println("Generating customer number: $it with id: $id")
        Customer(faker.name().fullName(), faker.internet().emailAddress(), id)
    }.asSequence()
}

fun createEmployees(): Sequence<Employee> {
    val faker = Faker()

    return Array(5) {
        val id: UUID = UUID.randomUUID()
        println("Generating employee number: $it with id: $id")
        Employee(faker.name().fullName(), faker.internet().emailAddress(), LocalDate.now(), id)
    }.asSequence()
}