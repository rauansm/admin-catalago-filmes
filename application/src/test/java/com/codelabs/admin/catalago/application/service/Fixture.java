package com.codelabs.admin.catalago.application.service;

import com.codelabs.admin.catalago.common.utils.IdUtils;
import com.codelabs.admin.catalago.domain.castmember.CastMember;
import com.codelabs.admin.catalago.domain.category.Category;
import com.codelabs.admin.catalago.domain.enums.CastMemberType;
import com.codelabs.admin.catalago.domain.enums.Rating;
import com.codelabs.admin.catalago.domain.enums.VideoMediaType;
import com.codelabs.admin.catalago.domain.genre.Genre;
import com.codelabs.admin.catalago.domain.video.AudioVideoMedia;
import com.codelabs.admin.catalago.domain.video.ImageMedia;
import com.codelabs.admin.catalago.domain.video.Resource;
import com.codelabs.admin.catalago.domain.video.Video;
import com.github.javafaker.Faker;

import java.time.Year;
import java.util.List;
import java.util.Set;

public final class Fixture {

    private static final Faker FAKER = new Faker();

    public static String name() {
        return FAKER.name().fullName();
    }

    public static Integer year() {
        return FAKER.random().nextInt(2020, 2030);
    }

    public static Double duration() {
        return FAKER.options().option(120.0, 15.5, 35.5, 10.0, 2.0);
    }

    public static boolean bool() {
        return FAKER.bool().bool();
    }

    public static String title() {
        return FAKER.options().option(
                "A avalanche",
                "Os trapalhões",
                "Terra sem lei"
        );
    }

    public static String checksum() {
        return "03fe62de";
    }

    public static Video video() {
        return Video.newVideo(
                Fixture.title(),
                Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.bool(),
                Fixture.bool(),
                Videos.rating(),
                Set.of(Categories.filmes().getId()),
                Set.of(Genres.acao().getId()),
                Set.of(CastMembers.vinDisel().getId(), CastMembers.rauan().getId())
        );
    }

    public static final class Categories {

        private static final Category FILMES =
                Category.newCategory("Aulas", "Some description", true);

        private static final Category SERIES =
                Category.newCategory("Lives", "Some description", true);

        public static Category filmes() {
            return FILMES.clone();
        }

        public static Category series() {
            return SERIES.clone();
        }
    }

    public static final class CastMembers {

        private static final CastMember VIN_DISEL =
                CastMember.newMember("Vin Disel", CastMemberType.ACTOR);

        private static final CastMember RAUAN =
                CastMember.newMember("Rauan", CastMemberType.ACTOR);

        public static CastMemberType type() {
            return FAKER.options().option(CastMemberType.values());
        }

        public static CastMember vinDisel() {
            return CastMember.with(VIN_DISEL);
        }

        public static CastMember rauan() {
            return CastMember.with(RAUAN);
        }
    }

    public static final class Genres {

        private static final Genre ACAO =
                Genre.newGenre("Ação", true);

        private static final Genre SUSPENSE =
                Genre.newGenre("Suspense", true);

        public static Genre acao() {
            return Genre.with(ACAO);
        }

        public static Genre suspense() {
            return Genre.with(SUSPENSE);
        }
    }

    public static final class Videos {

        private static final Video CORRIDA_MORTAL = Video.newVideo(
                "Corrida Mortal",
                description(),
                Year.of(2022),
                Fixture.duration(),
                Fixture.bool(),
                Fixture.bool(),
                rating(),
                Set.of(Categories.filmes().getId()),
                Set.of(Genres.acao().getId()),
                Set.of(CastMembers.vinDisel().getId(), CastMembers.rauan().getId())
        );

        public static Video corridaMortal() {
            return Video.with(CORRIDA_MORTAL);
        }

        public static Rating rating() {
            return FAKER.options().option(Rating.values());
        }

        public static VideoMediaType mediaType() {
            return FAKER.options().option(VideoMediaType.values());
        }

        public static Resource resource(final VideoMediaType type) {
            List<VideoMediaType> types = List.of(VideoMediaType.VIDEO, VideoMediaType.TRAILER);
            final String contentType = types.contains(type) ? "video/mp4" : "image/jpg";

            final String checksum = IdUtils.uuid();
            final byte[] content = "Conteudo".getBytes();

            return Resource.with(content, checksum, contentType, type.name().toLowerCase());
        }

        public static String description() {
            return FAKER.options().option(
                    """
                            Um ex-piloto é condenado por um crime brutal que não cometeu.
                            Na prisão, ele é obrigado a participar de uma competição mortal,
                            onde o vencedor obtém a liberdade.
                            """,
                    """
                            Uma diretora de prisão força o ex-presidiário Jensen Ames
                            a competir no esporte mais popular no mundo pós-industrial:
                            uma corrida de carros na qual os prisioneiros devem brutalizar
                            e matar uns aos outros em seu caminho para a vitória.
                            """
            );
        }

        public static AudioVideoMedia audioVideo(final VideoMediaType type) {
            final var checksum = Fixture.checksum();
            return AudioVideoMedia.with(
                    checksum,
                    type.name().toLowerCase(),
                    "/videos/" + checksum
            );
        }

        public static ImageMedia image(final VideoMediaType type) {
            final var checksum = Fixture.checksum();
            return ImageMedia.with(
                    checksum,
                    type.name().toLowerCase(),
                    "/images/" + checksum
            );
        }
    }
}
