package com.atguigu.bigdata.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming01_WordCount {
  def main(args: Array[String]): Unit = {

    //使用StreamingContext完成wordCount

    //创建环境
    val sparkConf: SparkConf = new SparkConf().setAppName("WordCount").setMaster("local[*]")
    //创建SparkStreaming上下文对象
    //第二个参数表示数据采集的周期
    val context: StreamingContext = new StreamingContext(sparkConf,Seconds(3))

    //从指定的端口获取数据
    val socketDStream: ReceiverInputDStream[String] = context.socketTextStream("hadoop102",9999)

    //将每一行的数据进行扁平化操作
    val lineDStream: DStream[String] = socketDStream.flatMap {
      line => {
        line.split(" ")
      }
    }

    //将每个单词进行结构的转换
    val wordToOneDStream: DStream[(String, Int)] = lineDStream.map {
      word => (word, 1)
    }

    //计算出每个单词出现的个数
    val wordToSumDStream: DStream[(String, Int)] = wordToOneDStream.reduceByKey(_+_)

    //打印结果
    wordToSumDStream.print()

//    wordToOneDStream.foreachRDD(rdd=>{})

    //启动采集器
    context.start()

    //driver等待采集器结束
    context.awaitTermination()
  }
}
