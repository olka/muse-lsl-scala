package org.olka.eeg

import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigFactory
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.{DEFAULT_CODEC_REGISTRY, Macros}
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.{MongoClient, MongoCollection}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object MongoHelper {
  val config = ConfigFactory.load()
  val rawUrl = config.getString("mongo.url")
  val url = rawUrl.substring(0,rawUrl.lastIndexOf("/")+1)
  val databaseName = rawUrl.substring(rawUrl.lastIndexOf("/")+1)
  val mongoClient: MongoClient = MongoClient(rawUrl)
  val responseRegistry = fromRegistries(fromProviders(classOf[EEG]), DEFAULT_CODEC_REGISTRY)
  val database = mongoClient.getDatabase(databaseName).withCodecRegistry(responseRegistry)
  val collection: MongoCollection[EEG] = database.getCollection("eeg")
  def size = Await.result(collection.count().head(), Duration(10, TimeUnit.SECONDS)).toInt
  def getLatest:List[EEG] = Await.result(collection.find().skip(size-1).toFuture(), Duration(10, TimeUnit.SECONDS)).toList
  def getAll(limit: Int) = Await.result(collection.find().limit(limit).toFuture(), Duration(10, TimeUnit.SECONDS)).toList
  def persist(eeg: EEG) = Await.result(collection.insertOne(eeg).head(), Duration(2, TimeUnit.SECONDS))
  def deleteAll() = Await.result(collection.drop().head(),Duration(10, TimeUnit.SECONDS))
}
