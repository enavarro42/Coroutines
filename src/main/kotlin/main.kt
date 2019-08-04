import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

fun main(args: Array<String>){
    //coroutineBasic()
    //coroutineBasicRunBlocking()
    //waitingForAJob()
    //coroutineScopeBuilder()
    //launch100kCoroutines()
    //cancellingCoroutine()
    //timeoutCoroutine() // Exception in thread "main" kotlinx.coroutines.TimeoutCancellationException: Timed out waiting for 1300 ms
    //exampleBlockingDispatcher()
    //exampleLaunchGlobal()
    //exampleAsyncAwait()
    exampleWithContext()

    /* Channels Examples */

    //channelBasic()
}

suspend fun printlnDelayed(message: String) {
    // Complex calculation
    delay(1000)
    println(message)
}

fun coroutineBasic(){
    GlobalScope.launch { // launch new coroutine in background and continue
        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
        println("World!") // print after delay
    }
    println("Hello,") // main thread continues while coroutine is delayed
    Thread.sleep(2000L) // block main thread for 2 seconds to keep JVM alive
}

fun coroutineBasicRunBlocking() = runBlocking {
    GlobalScope.launch { // launch new coroutine in background and continue
        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
        println("World!") // print after delay
    }
    println("Hello,") // main thread continues while coroutine is delayed
    delay(2000L) // block main thread for 2 seconds to keep JVM alive
}

fun waitingForAJob() = runBlocking {
    val job = GlobalScope.launch { // launch new coroutine and keep a reference to its Job
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    job.join() // wait until child coroutine completes
}

// Scope Builder
fun coroutineScopeBuilder() = runBlocking { // this: CoroutineScope
    launch {
        delay(200L)
        println("Task from runBlocking")
    }
    coroutineScope { // Creates a new coroutine scope
        launch {
            delay(500L)
            println("Task from nested launch")
        }
        delay(100L)
        println("Task from coroutine scope") // This line will be printed before nested launch
    }
    println("Coroutine scope is over") // This line is not printed until nested launch completes
}

fun launch100kCoroutines() = runBlocking {
    repeat(100_000) { // launch a lot of coroutines
        launch {
            delay(1000L)
            print("Coroutine!")
        }
    }
}

fun coroutineTwo() = runBlocking { // 1
    launch {
        delay(200L)
        println("After 200-millisecond delay.")
    }
    // 2
    coroutineScope {
        // 3
        launch {
            delay(500L)
            println("After 500-millisecond delay.")
        }
        delay(100L)
        println("After 100-millisecond delay.")
        println("${perform200msTask ()}")
    }
    // 3
    println("...and we're done!")
}
// 4
suspend fun perform200msTask(): String {
    delay(200L)
    return "Finished performing a 200ms task."
}

fun cancellingCoroutine() = runBlocking {
    val job = launch {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancel() // cancels the job
    job.join() // waits for job's completion
    println("main: Now I can quit.")
}

suspend fun calculateHardThings(startNum: Int): Int {
    delay(1000)
    return startNum * 10
}
// execute multiple request same time (Async)
fun exampleAsyncAwait() = runBlocking {
    val startTime = System.currentTimeMillis()

    val deferred1 = async { calculateHardThings(10) }
    val deferred2 = async { calculateHardThings(20) }
    val deferred3 = async { calculateHardThings(30) }

    val sum = deferred1.await() + deferred2.await() + deferred3.await()
    println("async/await result = $sum")

    val endTime = System.currentTimeMillis()
    println("Time taken: ${endTime - startTime}")
}

// execute not async
fun exampleWithContext() = runBlocking {
    val startTime = System.currentTimeMillis()

    val result1 = withContext(Dispatchers.Default) { calculateHardThings(10) }
    val result2 = withContext(Dispatchers.Default) { calculateHardThings(20) }
    val result3 = withContext(Dispatchers.Default) { calculateHardThings(30) }

    val sum = result1 + result2 + result3
    println("async/await result = $sum")

    val endTime = System.currentTimeMillis()
    println("Time taken: ${endTime - startTime}")
}

// Dispatcher

// Running on another thread but still blocking the main thread
fun exampleBlockingDispatcher() {
    runBlocking(Dispatchers.Default) {
        println("one - from thread ${Thread.currentThread().name}")
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }
    // Outside of runBlocking to show that it's running in the blocked main thread
    println("three - from thread ${Thread.currentThread().name}")
    // It still runs only after the runBlocking is fully executed.
}

// not block main thread
fun exampleLaunchGlobal() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    GlobalScope.launch {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")
    delay(3000)
}

//Timeout

fun timeoutCoroutine() = runBlocking {
    withTimeout(1300L) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
}

fun withTimeoutOrNullCoroutine() = runBlocking {
    val result = withTimeoutOrNull(1300L) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
        "Done" // will get cancelled before it produces this result
    }
    println("Result is $result")
}

// Channels

fun channelBasic() = runBlocking {
    val channel = Channel<Int>()
    launch {    // this might be heavy CPU-consuming computation or async logic, we'll just send five squares
        for (x in 1..5)
            channel.send(x * x)
    } // here we print five received integers:
    repeat(5) {
        println(channel.receive())
    }
    println("Done!")
}


//------------------------------------------kotlin 1.3

fun fastCheapOrGood(): String {
    return when (1) {
        0 -> "Fast."
        1 -> "Cheap."
        2 -> "Good."
        else -> "None of the standard outcomes."
    }
}

fun testNullEmpty(){
    val nullArray: Array<String>? = null
    val emptyArray = arrayOf<String>()
    val filledArray = arrayOf("Alpha", "Bravo", "Charlie")

    println("nullArray.isNullOrEmpty(): ${nullArray.isNullOrEmpty()}") // true
    println("emptyArray.isNullOrEmpty(): ${emptyArray.isNullOrEmpty()}") // true
    println("filledArray.isNullOrEmpty(): ${filledArray.isNullOrEmpty()}") // false
    println("nullArray.orEmpty(): ${(nullArray.orEmpty()).joinToString()}") // []
    println("filledArray.orEmpty(): ${filledArray.orEmpty().joinToString()}") // ["Alpha", "Bravo", "Charlie"]
}