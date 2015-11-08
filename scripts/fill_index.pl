#!/usr/bin/perl

use strict;
use warnings;
use feature 'say';

use Mojo::UserAgent;
use Data::Dumper;
use XML::Simple;


my $ua = Mojo::UserAgent->new;
my $xs = XML::Simple->new;

my $body = "<bbox-query s=\"55.72\" n=\"55.85\" w=\"37.41\" e=\"37.82\"/>
         <query type=\"node\">
            <item/>
            <has-kv k=\"amenity\" v=\"cafe\"/>
            <has-kv k=\"cuisine\" v=\"coffee_shop\"/>
         </query>
         <print/>";


my $xml = $ua->post("http://overpass-api.de/api/interpreter" => {Accept => '*/*'} => $body)->res->body;

say "found: ".scalar(keys %{$xs->XMLin($xml)->{node}});

foreach my $id (%{$xs->XMLin($xml)->{node}}) {
    print ".";
    # say $id;

    my $tags    = $xs->XMLin($xml)->{node}{$id}{tag};
    my $name    = get_tag('name:ru', @$tags) || get_tag('name', @$tags);
    my $lat     = $xs->XMLin($xml)->{node}{$id}{lat};
    my $lon     = $xs->XMLin($xml)->{node}{$id}{lon};

    if($name && $lat && $lon) {
        # say $name;
        # say $lat;
        # say $lon;

        my $tx = $ua->put("http://localhost:9200/map/coffee/$id" => {Accept => '*/*'} => json => { 
            "name" => $name,
            "location" => {
                "lat" => $lat, 
                "lon" => $lon
                }
            });
    }

    # say Dumper $tx;

    # say $xs->XMLin($xml)->{node}{$id}{tag}
    # last;
}

sub get_tag {
    my ($name, @a) = @_;

    my @tag = grep { $_->{k} eq $name } @a;

    return $tag[0]->{v};
}