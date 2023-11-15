import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.random.Random

suspend fun main(args: Array<String>) {
    val concurrencyLimiter = ConcurrencyLimiter(maxConcurrentJobs = 3) // Limit to 3 concurrent jobs
    val jobIds = (1..10).toList() // List of job IDs from 1 to 10

    val results = concurrencyLimiter.processJobs(jobIds)
    results.forEach { result ->
        println(result)
    }
}

class ConcurrencyLimiter(
    maxConcurrentJobs: Int
) {
    private val semaphore = Semaphore(maxConcurrentJobs)

    // Function to simulate a job with a random delay
    private suspend fun performJob(jobId: Int): String {
        val delayTime = Random.nextLong(1000L, 5000L) // Random delay between 1 and 5 seconds
        delay(delayTime)
        println("Job $jobId completed after ${delayTime}ms")
        return "Result of job $jobId"
    }

    // Function to process multiple jobs concurrently with a limit on the number of concurrent jobs
    suspend fun processJobs(jobIds: List<Int>): List<String> = coroutineScope {
        jobIds.map { jobId ->
            async {
                semaphore.withPermit {
                    performJob(jobId)
                }
            }
        }.awaitAll()
    }
}
