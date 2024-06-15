package cat.fpp.tiporhang.data.models.utils

import cat.fpp.tiporhang.R

enum class ImageIndex(val value: Int, val src: Int) {
    FRAME0( 0, R.drawable.toh_frame_0),
    FRAME1( 1, R.drawable.toh_frame_1),
    FRAME2( 2, R.drawable.toh_frame_2),
    FRAME3( 3, R.drawable.toh_frame_3),
    FRAME4( 4, R.drawable.toh_frame_4),
    FRAME5( 5, R.drawable.toh_frame_5),
    FRAME6( 6, R.drawable.toh_frame_6),
    GAME_OVER(7, R.drawable.toh_frame_gameover),
    YOU_WIN( 8, R.drawable.toh_frame_youwin),
    TITLE( 9, R.drawable.toh_frame_title)
}
