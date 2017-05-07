package ske.aurora.utils

import static ske.aurora.utils.PrometheusUrlNormalizer.normalize

import spock.lang.Specification
import spock.lang.Unroll

class PrometheusUrlNormalizerTest extends Specification {

  @Unroll
  def "should normalize url #url for prometheus"() {

    expect:
      normalize(url) == normalized
      normalize(url) == normalize(url)

    where:
      url                                   | normalized
      "http://www.vg.no"                    | "www.vg.no"
      "https://www.vg.no:8443"              | "www.vg.no_8443"
      //denne filer med "vanlig normalize"
      "http://www.vg.no/index.html?foo=bar" | "www.vg.no_index.html"
      "https://int-ref.skead.no:14110/felles/sikkerhet/stsSikkerhet/v2/utstedSaml" | "int_ref.skead.no_14110_felles_sikkerhet_stsSikkerhet_v2_utstedSaml"
  }
}