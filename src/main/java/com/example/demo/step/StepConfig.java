package com.example.demo.step;

import java.io.Console;
import java.time.LocalDate;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.demo.model.Message;
import com.example.demo.service.MessageService;

@Configuration
public class StepConfig {

    @Autowired
    private MessageService messageService;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

        @Bean
        @StepScope
    public Step uploadCsvStep(@Value("#{jobParameters['file_name']}") String fileName) {
        return stepBuilderFactory.get("uploadCsvStep")
            .<Message, Message>chunk(10)
            .reader(readUploadedCsv(fileName))
            .processor((ItemProcessor<Message, Message>) item -> {
                return checkMessage(item); // ここでは何も処理しない
            }) 
            .writer(items -> items.forEach(item -> 
                messageService.saveMessage(
                    item.getDate(), 
                    item.getStartTime(), 
                    item.getEndTime(), 
                    item.getTitle(), 
                    item.getCategory()
                )
            ))
            .build();
    }

    @StepScope
    public Message checkMessage(Message item) {
        Console console = System.console();
        if (item.getTitle() == null ||  item.getDate() == null || item.getStartTime() == null || item.getEndTime() == null || item.getCategory() == null) {
            console.printf("Invalid message: %s\n", "null");
            return null;
        }
        if (item.getTitle().isEmpty() || item.getDate().isEmpty() || item.getStartTime().isEmpty() || item.getEndTime().isEmpty() || item.getCategory().isEmpty()) {
            console.printf("Invalid message: %s\n", "null");
            return null; // 必須項目が空の場合は無効
        }
        if (item.getStartTime().compareTo(item.getEndTime()) >= 0) {
            console.printf("Invalid message: %s ,%s\n", item.getStartTime(), item.getEndTime());
            return null; // 開始時間が終了時間以上の場合は無効
        }
        if (item.getDate().length() != 10 || !item.getDate().matches("\\d{4}/\\d{2}/\\d{2}")) {
            console.printf("Invalid message: %s\n", item.getDate());
            return null; // 日付のフォーマットが不正な場合は無効
        }
        if (item.getStartTime().length() != 5 || !item.getStartTime().matches("\\d{2}:\\d{2}")) {
            console.printf("Invalid message: %s\n", item.getStartTime());
            return null; // 開始時間のフォーマットが不正な場合は無効
        }
        if (item.getEndTime().length() != 5 || !item.getEndTime().matches("\\d{2}:\\d{2}")) {
            console.printf("Invalid message: %s\n", item.getEndTime());
            return null; // 終了時間のフォーマットが不正な場合は無効
        }
        if (!item.getCategory().equals("improvement") && !item.getCategory().equals("society") && !item.getCategory().equals("life") && !item.getCategory().equals("entertainment") && !item.getCategory().equals("other")) {
            console.printf("Invalid message: %s\n", item.getCategory());
            return null;
        }
        if (item.getTitle().length() > 20) {
            console.printf("Invalid message: %s\n", item.getTitle());
            return null; // タイトルが20文字を超える場合は無効
        }

        int year = Integer.parseInt(item.getDate().substring(0, 4));
        int month = Integer.parseInt(item.getDate().substring(5, 7));
        int day = Integer.parseInt(item.getDate().substring(8, 10));
        int startHour = Integer.parseInt(item.getStartTime().substring(0, 2));
        int startMinute = Integer.parseInt(item.getStartTime().substring(3, 5));
        int endHour = Integer.parseInt(item.getEndTime().substring(0, 2));
        int endMinute = Integer.parseInt(item.getEndTime().substring(3, 5));
        if (year < 2000) {
            console.printf("Invalid message: %s\n", item.getDate());
            return null; // 年が2000年未満の場合は無効
        }
        if (month < 1 || month > 12 || day < 1 || day > LocalDate.of(year, month, 1).lengthOfMonth()) {
            console.printf("Invalid message: %s\n", item.getDate());
            return null; // 月または日が不正な場合は無効
        }
        if (startHour < 0 || startHour > 23 || startMinute < 0 || startMinute > 59 || endHour < 0 || endHour > 23 || endMinute < 0 || endMinute > 59) {
            console.printf("Invalid message: %s ,%s\n", item.getStartTime(), item.getEndTime());
            return null; // 時間の範囲が不正な場合は無効
        }
        if (LocalDate.of(year, month, day).isAfter(LocalDate.now())) {
            console.printf("Invalid message: %s\n", item.getDate());
            return null; // 日付が未来の場合は無効
        }
        if (!messageService.isTimeConflict(item.getDate(), item.getStartTime(), item.getEndTime())) {
            console.printf("Invalid message: conflict time %s ,%s\n", item.getStartTime(), item.getEndTime());
            return null; // 時間の重複がある場合は無効
        }

        return item;
    }

    @StepScope
    public FlatFileItemReader<Message> readUploadedCsv(String fileName) {   
        return new FlatFileItemReaderBuilder<Message>()
            .name("csvReader")
            .resource(new FileSystemResource(fileName))
            .lineMapper(new DefaultLineMapper<>() {{
                setLineTokenizer(new DelimitedLineTokenizer() {{
                    setNames("name", "date", "startTime", "endTime", "category");
                }});

                setFieldSetMapper(fieldSet -> {
                    Message message = new Message();
                    message.setDate(fieldSet.readString("date"));
                    message.setStartTime(fieldSet.readString("startTime"));
                    message.setEndTime(fieldSet.readString("endTime"));
                    message.setTitle(fieldSet.readString("name"));
                    message.setCategory(fieldSet.readString("category"));
                    return message;
                }); // 必要に応じて変更
            }})
            .build();
        }
}
