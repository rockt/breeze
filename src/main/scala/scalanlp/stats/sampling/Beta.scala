package scalanlp.stats.sampling

import math.Numerics.lgamma;
import Math._;
import counters._;

class Beta(a: Double, b: Double) extends ContinuousDistr[Double] with ConjugatePrior[Double, Boolean]{
  require(a > 0.0);
  require(b > 0.0);
  def pdf(x: Double) = exp(logPdf(x));
  
  override def logPdf(x: Double) = {
    logNormalizer + unnormalizedLogPdf(x);
  }
  
  override def unnormalizedLogPdf(x: Double) = {
    require(x >= 0);
    require(x <= 1)
    (a-1) * log(x) + (b-1) * log(1-x)
  }
  
  val logNormalizer = lgamma(a+b) - lgamma(a) - lgamma(b)
  
  private val aGamma = new Gamma(a,1);
  private val bGamma = new Gamma(b,1.);
  
  override def draw() = {
    val ad = aGamma.get;
    val bd = bGamma.get;
    (ad) / (ad + bd);
  }
  
  def posterior(obs: Iterator[(Boolean,Int)]) = {
    val ctr = Counters.aggregate(obs);
    new Beta(ctr(true)+a, ctr(false)+b)
  }
  
  def predictive = {
    val ctr = DoubleCounter[Boolean]();
    ctr(true) = a;
    ctr(false) = b;
    new Polya(ctr);
  }
  

}