package com.example.truyenchuvietsub.model.enums;

public enum EnumGenre {

    ACTION {
        private final String name = "Hành động";
        private final String shortDescription = "Các tình tiết hành động, võ thuật, đánh nhau.";
        private final String longDescription = "Thể loại Action tập trung vào các tình tiết hành động, các kỹ năng võ thuật và các pha hành động mạo hiểm, đôi khi kèm theo các trận chiến gay cấn, hấp dẫn.";
    },
    ADULT {
        private final String name = "Người lớn";
        private final String shortDescription = "Nội dung không phù hợp cho độc giả dưới 18 tuổi.";
        private final String longDescription = "Thể loại Adult chứa nội dung không phù hợp cho độc giả dưới 18 tuổi, thường bao gồm các yếu tố nhạy cảm, tình huống tưởng tượng người lớn, cảnh tượng hoặc diễn biến không phù hợp cho độc giả trẻ.";
    },
    ADVENTURE {
        private final String name = "Phiêu lưu";
        private final String shortDescription = "Cuộc phiêu lưu, khám phá, hành trình.";
        private final String longDescription = "Thường mô tả cuộc phiêu lưu, khám phá, hành trình của nhân vật chính trong một thế giới mới lạ, thường là thế giới huyền bí, kỳ ảo.";

    },
    BOYS_LOVE {
        private final String name = "Đam mỹ";
        private final String shortDescription = "Tình yêu giữa nam với nam.";
        private final String longDescription = "";
    },
    COMEDY {
        private final String name = "Hài hước";
        private final String shortDescription = "Các tình tiết hài hước, vui nhộn.";
        private final String longDescription = "Thường có các tình tiết hài hước, vui nhộn, mang lại cảm giác thoải mái, dễ chịu cho người đọc.";
    },
    DRAMA {
        private final String name = "Kịch tính";
        private final String shortDescription = "Các tình tiết kịch tính, xúc động.";
        private final String longDescription = "Thường có các tình tiết kịch tính, xúc động, mang lại cảm giác căng thẳng, hồi hộp cho người đọc.";
    },
    DOUJINSHI {
        private final String name = "Doujinshi";
        private final String shortDescription = "Truyện tranh do các tác giả tự do sáng tác.";
        private final String longDescription = "Thể loại Doujinshi là truyện tranh do các tác giả tự do sáng tác, thường được xuất bản bởi các nhóm dịch, không phải là nhà xuất bản chuyên nghiệp.";
    }, // Doujinshi
    ECCHI {
        private final String name = "Ecchi";
        private final String shortDescription = "Chứa nội dung người lớn nhẹ nhàng.";
        private final String longDescription = "Thể loại Ecchi chứa nội dung người lớn nhẹ nhàng, thường có yếu tố tình dục nhưng không quá rõ ràng, thường gợi cảm mà không hở hang.";
    }, // Ecchi
    FANFICTION {
        private final String name = "Fanfiction";
        private final String shortDescription = "Truyện dựa trên các tác phẩm đã có.";
        private final String longDescription = "Thể loại Fanfiction là truyện dựa trên các tác phẩm đã có, thường là các tác phẩm nổi tiếng như truyện tranh, tiểu thuyết, phim ảnh, trò chơi điện tử, v.v.";
    }, // Fanfiction
    FANTASY {
        private final String name = "Fantasy";
        private final String shortDescription = "Thế giới huyền bí, kỳ ảo.";
        private final String longDescription = "Thể loại Fantasy tập trung vào thế giới tưởng tượng, ma thuật, các sinh vật huyền bí và các yếu tố phi thực tế.";
    },
    GAME {
        private final String name = "Game";
        private final String shortDescription = "Liên quan đến trò chơi, game.";
        private final String longDescription = "Thể loại Game liên quan đến các yếu tố trong trò chơi, game, thường xoay quanh các cốt truyện, yếu tố hành động từ thế giới game.";
    },
    GENDER_BENDER {
        private final String name = "Gender Bender";
        private final String shortDescription = "Chuyển đổi giới tính của nhân vật.";
        private final String longDescription = "Thể loại Gender Bender tập trung vào việc chuyển đổi giới tính của nhân vật chính, thường là việc biến nam thành nữ hoặc nữ thành nam.";
    },
    GIRLS_LOVE {
        private final String name = "Girls Love";
        private final String shortDescription = "Tập trung vào mối quan hệ tình cảm giữa nữ giới.";
        private final String longDescription = "Thể loại Girls Love (GL) tập trung vào mối quan hệ tình cảm giữa nữ giới, thường có yếu tố lãng mạn hoặc đời thường.";
    },
    HAREM {
        private final String name = "Harem";
        private final String shortDescription = "Nhân vật chính được quyến rũ bởi nhiều người theo.";
        private final String longDescription = "Thể loại Harem tập trung vào nhân vật chính được quyến rũ bởi nhiều người theo, thường là nhiều nhân vật đối tượng.";
    },
    HISTORICAL {
        private final String name = "Lịch sử";
        private final String shortDescription = "Liên quan đến lịch sử, cốt truyện trong quá khứ.";
        private final String longDescription = "Thể loại Historical liên quan đến lịch sử, cốt truyện thường diễn ra trong quá khứ, có thể xoay quanh sự kiện lịch sử, văn hoá, xã hội.";
    },
    HORROR {
        private final String name = "Kinh dị";
        private final String shortDescription = "Chứa yếu tố kinh dị, ma quái, huyền bí.";
        private final String longDescription = "Thể loại Horror chứa yếu tố kinh dị, ma quái, huyền bí, thường tạo cảm giác lo sợ, hồi hộp cho người đọc.";
    },
    ISEKAI {
        private final String name = "Isekai";
        private final String shortDescription = "Chuyển sinh sang một thế giới khác.";
        private final String longDescription = "Thể loại Isekai thường liên quan đến việc nhân vật chính chuyển sinh, xuyên không sang một thế giới khác so với thế giới hiện tại.";
    },
    JOSEI {
        private final String name = "Josei";
        private final String shortDescription = "Dành cho phụ nữ trưởng thành.";
        private final String longDescription = "Thể loại Josei là các tác phẩm dành cho độc giả phụ nữ trưởng thành, thường tập trung vào các vấn đề cuộc sống hàng ngày, tình cảm và mối quan hệ.";
    },
    LITRPG {
        private final String name = "";
        private final String shortDescription = "";
        private final String longDescription = "";
    },
    MARTIAL_ARTS {
        private final String name = "Võ thuật";
        private final String shortDescription = "Liên quan đến võ thuật, võ đạo.";
        private final String longDescription = "Liên quan đến võ thuật, võ đạo, thường tập trung vào các yếu tố chiến đấu, võ công và cốt truyện xoay quanh võ thuật.";
    },
    MATURE {
        private final String name = "Trưởng thành";
        private final String shortDescription = "Chứa nội dung người lớn.";
        private final String longDescription = "Thể loại Mature chứa nội dung dành cho độc giả trưởng thành, thường có các yếu tố nhạy cảm, tình dục hoặc bạo lực.";
    },
    MECHA {
        private final String name = "Mecha";
        private final String shortDescription = "Liên quan đến robot, cơ giới.";
        private final String longDescription = "Thể loại Mecha liên quan đến robot, cơ giới, thường xoay quanh các yếu tố về robot, máy móc hoặc những trận chiến giữa các robot.";
    },
    MYSTERY {
        private final String name = "Huyền bí";
        private final String shortDescription = "Thám tử, điều tra vụ án bí ẩn.";
        private final String longDescription = "Thể loại Mystery tập trung vào việc giải quyết các vụ án bí ẩn, điều tra, hoặc những sự kiện không thể giải thích bằng lý trí.";
    },
    PSYCHOLOGICAL {
        private final String name = "Tâm lý học";
        private final String shortDescription = "Liên quan đến tâm lý học.";
        private final String longDescription = "hể loại Psychological liên quan đến tâm lý học của nhân vật, thường khám phá và đào sâu vào tâm trạng, suy nghĩ, hành động của họ.";
    },
    ROMANCE {
        private final String name = "Tình cảm";
        private final String shortDescription = "Tình cảm, mối quan hệ.";
        private final String longDescription = "Thể loại Romance tập trung vào tình cảm, mối quan hệ giữa các nhân vật chính, thường có yếu tố lãng mạn và tình yêu.";
    },
    SCHOOL_LIFE {
        private final String name = "Học đường";
        private final String shortDescription = "Cốt truyện diễn ra trong trường học.";
        private final String longDescription = "Có cốt truyện diễn ra trong môi trường học đường, thường tập trung vào cuộc sống học đường, tình bạn, tình yêu trong trường.";
    },
    SCI_FI {
        private final String name = "Khoa học viễn tưởng";
        private final String shortDescription = "Khoa học viễn tưởng, công nghệ.";
        private final String longDescription = "Thể loại Sci-Fi tập trung vào yếu tố khoa học viễn tưởng, công nghệ tiên tiến, thế giới tương lai hoặc các yếu tố khoa học khác.";
    },
    SEINEN {
        private final String name = "Seinen";
        private final String shortDescription = "Dành cho nam giới trưởng thành.";
        private final String longDescription = "Thể loại Seinen là các tác phẩm dành cho độc giả nam trưởng thành, thường tập trung vào các vấn đề lớn hơn về cuộc sống và xã hội.";
    },
    SHOUNEN {
        private final String name = "Shounen";
        private final String shortDescription = "Dành cho nam thiếu niên.";
        private final String longDescription = "Thể loại Shounen là các tác phẩm dành cho độc giả nam thiếu niên, thường tập trung vào những cuộc phiêu lưu, hành động, và trận chiến.";
    }, // Shounen
    SLICE_OF_LIFE {
        private final String name = "Đời thường";
        private final String shortDescription = "Cảnh quan đời thường.";
        private final String longDescription = "Tập trung vào cảnh quan đời thường, thường mô tả cuộc sống hàng ngày, nhẹ nhàng, đời thực.";
    },
    SHOUJO {
        private final String name = "";
        private final String shortDescription = "Dành cho nữ thiếu niên.";
        private final String longDescription = "Thể loại Shoujo là các tác phẩm dành cho độc giả nữ thiếu niên, thường có yếu tố lãng mạn, tình cảm và đời thường.";
    },
    SPORTS {
        private final String name = "Thể thao";
        private final String shortDescription = "Liên quan đến thể thao.";
        private final String longDescription = "Liên quan đến thể thao, thường tập trung vào các hoạt động thể thao, cạnh tranh, và dành cho độc giả yêu thích thể thao.";
    },
    SUPERNATURAL {
        private final String name = "Siêu nhiên";
        private final String shortDescription = "Chứa yếu tố siêu nhiên, viễn tưởng.";
        private final String longDescription = "hể loại Supernatural chứa yếu tố siêu nhiên, viễn tưởng, thường liên quan đến hiện tượng và sức mạnh ngoại vi.";
    },
    TRAGEDY {
        private final String name = "Bi kịch";
        private final String shortDescription = "Kết cục bi thương, bi kịch.";
        private final String longDescription = "Có kết cục bi thương, bi kịch, thường tập trung vào những sự kiện không may mắn, đau buồn và kết thúc đầy tiếc nuối.";
    },

    // JAPAN


    // CHINA
    XIANXIA {
        private final String name = "Tiên hiệp";
        private final String shortDescription = "Liên quan đến Tiên hiệp.";
        private final String longDescription = "Thường liên quan đến thế giới siêu nhiên, các cảnh vật đặc trưng và yếu tố văn hóa Trung Quốc.";
    }, // Tiên hiệp
    WUXIA {
        private final String name = "Kiếm hiệp";
        private final String shortDescription = "Liên quan đến Kiếm hiệp.";
        private final String longDescription = "Thể loại Wuxia tập trung vào các chiến binh võ thuật, võ sĩ võ đạo, và các cuộc phiêu lưu hành động.";
    }, // Kiếm hiệp
    CULTIVATION {
        private final String name = "Tu tiên";
        private final String shortDescription = "Liên quan đến Tu chân.";
        private final String longDescription = "Liên quan đến việc tu luyện, nâng cao trình độ cơ bản và đạt được sức mạnh phi thường.";
    }, // Tu chân
    XUANHUAN {
        private final String name = "Huyền huyễn";
        private final String shortDescription = "Liên quan đến Huyền huyễn.";
        private final String longDescription = "Liên quan đến thế giới huyền bí, ma thuật và những sức mạnh đặc biệt.";
    }, // Huyền huyễn
    URBAN {
        private final String name = "Đô thị";
        private final String shortDescription = "Liên quan đến Đô thị.";
        private final String longDescription = "Tập trung vào cuộc sống, câu chuyện diễn ra trong môi trường đô thị, thường có yếu tố thực tế.";
    }, // Đô thị
    ANCIENT {
        private final String name = "Cổ đại";
        private final String shortDescription = "Liên quan đến thời kỳ Cổ đại.";
        private final String longDescription = "Thể loại Ancient tập trung vào thời kỳ lịch sử cổ xưa, với các phong tục, văn hóa và cách sống khác biệt.";
    }, // Cổ đại
    TRANSMIGRATION {
        private final String name = "Xuyên không";
        private final String shortDescription = "Liên quan đến Xuyên không.";
        private final String longDescription = "Thường có yếu tố nhân vật chính xuyên qua thời gian hoặc không gian đến một thế giới khác.";
    }, // Xuyên không
}
