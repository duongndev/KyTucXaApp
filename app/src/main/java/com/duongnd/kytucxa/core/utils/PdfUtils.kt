package com.duongnd.kytucxa.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import com.duongnd.kytucxa.domain.models.FormFields
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PdfUtils {

    fun generateRegistrationPdf(context: Context, data: FormFields) {

        val pdf = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842

        val page = pdf.startPage(
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        )

        val canvas = page.canvas

        val margin = 40f
        var y = 40f

        // ===== Paint =====
        val normal = TextPaint().apply {
            textSize = 12f
            isAntiAlias = true
        }

        val bold = TextPaint().apply {
            textSize = 12f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val title = TextPaint().apply {
            textSize = 16f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        val italic = TextPaint().apply {
            textSize = 11f
            textSkewX = -0.25f
        }

        // ===== HEADER =====
        canvas.drawText(
            "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM",
            pageWidth / 2f,
            y,
            bold.apply { textAlign = Paint.Align.CENTER }
        )
        y += 18f

        canvas.drawText(
            "Độc lập – Tự do – Hạnh phúc",
            pageWidth / 2f,
            y,
            bold
        )
        y += 10f

        canvas.drawText(
            "-----------------------------",
            pageWidth / 2f,
            y,
            normal.apply { textAlign = Paint.Align.CENTER }
        )

        // ===== KHUNG ẢNH =====
        val rect = RectF(margin, 40f, margin + 90f, 140f)
        canvas.drawRect(rect, Paint().apply {
            style = Paint.Style.STROKE
        })

        val centerX = rect.centerX()
        canvas.drawText("Dán ảnh", centerX, 70f, normal.apply { textAlign = Paint.Align.CENTER })
        canvas.drawText("3x4", centerX, 85f, normal)
        canvas.drawText("Đóng dấu", centerX, 100f, normal)
        canvas.drawText("giáp lai của", centerX, 115f, normal)
        canvas.drawText("cơ sở đào tạo", centerX, 130f, normal)

        y = 170f

        // ===== TITLE =====
        canvas.drawText(
            "ĐƠN ĐĂNG KÝ THUÊ NHÀ Ở SINH VIÊN",
            pageWidth / 2f,
            y,
            title
        )

        y += 30f

        // ===== KÍNH GỬI =====
        canvas.drawText(
            "Kính gửi: BAN QUẢN LÝ CÁC CÔNG TRÌNH NHÀ Ở VÀ CÔNG SỞ",
            margin,
            y,
            bold.apply { textAlign = Paint.Align.LEFT }
        )

        y += 25f

        fun line(label: String, value: String): Float {
            canvas.drawText(label, margin, y, normal)
            canvas.drawText(
                value.ifEmpty { "..........................................................." },
                margin + normal.measureText(label) + 5,
                y,
                normal
            )
            return y + 20f
        }

        // ===== FORM =====
        y = line("Họ và tên người đăng ký: ", data.fullName)

        // Nam / Nữ checkbox
        canvas.drawText("Nam ☐", pageWidth - 150f, y - 20f, normal)
        canvas.drawText("Nữ ☐", pageWidth - 90f, y - 20f, normal)

        y = line("Ngày sinh: ", DateUtils.formatString(data.dob))
        y = line("CMND/CCCD số: ", data.idNumber)

        canvas.drawText("Ngày cấp: ", margin, y, normal)
        canvas.drawText(DateUtils.formatString(data.idIssueDate), margin + 80, y, normal)
        canvas.drawText("Nơi cấp: ", pageWidth / 2f, y, normal)
        canvas.drawText(data.idIssuePlace, pageWidth / 2f + 70, y, normal)
        y += 20f

        y = line("Hộ khẩu thường trú tại: ", data.permanentAddress)

        canvas.drawText("Số điện thoại liên hệ: ", margin, y, normal)
        canvas.drawText(data.phoneNumber, margin + 160, y, normal)
        canvas.drawText("Email: ", pageWidth / 2f, y, normal)
        canvas.drawText(data.email, pageWidth / 2f + 50, y, normal)
        y += 20f

        y = line("Khi cần liên hệ (báo tin): ", data.emergencyContact)
        y = line("Là sinh viên học tập tại cơ sở đào tạo: ", data.schoolName)

        canvas.drawText("Niên khóa: ", margin, y, normal)
        canvas.drawText(data.academicYear, margin + 90, y, normal)
        canvas.drawText("Lớp: ", margin + 200, y, normal)
        canvas.drawText(data.className, margin + 240, y, normal)
        canvas.drawText("Khoa: ", margin + 330, y, normal)
        canvas.drawText(data.department, margin + 380, y, normal)
        y += 20f

        y = line("Số thẻ sinh viên: ", data.studentId)
        y = line("Đối tượng ưu tiên (Nếu có): ", data.priorityType)

        // ===== NOTE =====
        canvas.drawText(
            "(Giấy chứng nhận ưu tiên kèm theo)",
            margin + 100,
            y,
            italic
        )

        y += 25f

        // ===== PARAGRAPH =====
        val text1 =
            "Tôi làm đơn này đề nghị: BAN QUẢN LÝ CÁC CÔNG TRÌNH NHÀ Ở VÀ CÔNG SỞ xét duyệt cho tôi được thuê nhà ở sinh viên tại KTX ${data.dormName}."

        val layout1 = StaticLayout.Builder.obtain(
            text1, 0, text1.length, normal, (pageWidth - 2 * margin).toInt()
        ).build()

        canvas.save()
        canvas.translate(margin, y)
        layout1.draw(canvas)
        canvas.restore()

        y += layout1.height + 10f

        val text2 =
            "Tôi đã đọc Bản nội quy sử dụng nhà ở sinh viên và cam kết tuân thủ nội quy, cam kết trả tiền thuê đầy đủ, đúng thời hạn."

        val layout2 = StaticLayout.Builder.obtain(
            text2, 0, text2.length, normal, (pageWidth - 2 * margin).toInt()
        ).build()

        canvas.save()
        canvas.translate(margin, y)
        layout2.draw(canvas)
        canvas.restore()

        y += layout2.height + 10f

        val text3 =
            "Tôi cam kết những lời khai trong đơn là đúng sự thật, tôi xin chịu trách nhiệm trước pháp luật."

        val layout3 = StaticLayout.Builder.obtain(
            text3, 0, text3.length, normal, (pageWidth - 2 * margin).toInt()
        ).build()

        canvas.save()
        canvas.translate(margin, y)
        layout3.draw(canvas)
        canvas.restore()

        y += layout3.height + 30f

        // ===== SIGN =====
        canvas.drawText(
            "Hà Nội, ngày ... tháng ... năm 20...",
            pageWidth - 250f,
            y,
            italic
        )

        y += 25f

        canvas.drawText(
            "Xác nhận của cơ sở đào tạo",
            margin + 60,
            y,
            bold
        )

        canvas.drawText(
            "Người viết đơn",
            pageWidth - 150f,
            y,
            bold
        )

        y += 15f

        canvas.drawText(
            "(Ký ghi ngày tháng và đóng dấu)",
            margin + 40,
            y,
            italic
        )

        canvas.drawText(
            "(Ký và ghi rõ họ tên)",
            pageWidth - 170f,
            y,
            italic
        )

        pdf.finishPage(page)

        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "DonDangKy_${data.studentId}.pdf"
        )

        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        Toast.makeText(context, "Đã tạo PDF", Toast.LENGTH_SHORT).show()
    }
}