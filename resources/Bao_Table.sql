USE it_course_management;

-- Bảng MyCart
CREATE TABLE MyCart (
    MyCartID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT NOT NULL
);

INSERT INTO MyCart (UserID) VALUES (1), (2), (3), (4), (5);

-- Bảng MyCartDetails
CREATE TABLE MyCartDetails (
    MyCartID INT,
    CourseID INT,
    IsBuy BIT DEFAULT 0,
    AddedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MyCartID, CourseID),
    FOREIGN KEY (MyCartID) REFERENCES MyCart(MyCartID)
);

INSERT INTO MyCartDetails (MyCartID, CourseID, IsBuy) VALUES 
(1, 101, 0), (2, 102, 1), (3, 103, 0), (4, 104, 1), (5, 105, 0);

-- Bảng Payments
CREATE TABLE Payments (
    PaymentID INT PRIMARY KEY AUTO_INCREMENT,
    PaymentName NVARCHAR(255) NOT NULL
);

INSERT INTO Payments (PaymentName) VALUES 
('Credit Card'), ('Paypal'), ('Bank Transfer'), ('Google Pay'), ('Apple Pay');

-- Bảng UserPayment
CREATE TABLE UserPayment (
    PaymentID INT,
    UserID INT,
    Balance FLOAT NOT NULL,
    PRIMARY KEY (PaymentID, UserID),
    FOREIGN KEY (PaymentID) REFERENCES Payments(PaymentID)
);

INSERT INTO UserPayment (PaymentID, UserID, Balance) VALUES 
(1, 1, 1000.50), (2, 2, 500.75), (3, 3, 1200.00), (4, 4, 300.25), (5, 5, 800.60);

-- Bảng Invoices
CREATE TABLE Invoices (
    InvoiceID INT PRIMARY KEY AUTO_INCREMENT,
    UserID INT NOT NULL,
    TotalPrice FLOAT NOT NULL,
    BoughtAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO Invoices (UserID, TotalPrice) VALUES 
(1, 199.99), (2, 299.49), (3, 149.89), (4, 99.99), (5, 349.99);

-- Bảng InvoiceDetail
CREATE TABLE InvoiceDetail (
    InvoiceID INT,
    CourseID INT,
    PRIMARY KEY (InvoiceID, CourseID),
    FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID)
);

INSERT INTO InvoiceDetail (InvoiceID, CourseID) VALUES 
(1, 101), (2, 102), (3, 103), (4, 104), (5, 105);

-- Bảng Notification
CREATE TABLE Notification (
    NotificationID INT PRIMARY KEY AUTO_INCREMENT,
    NotificationName VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    Content VARCHAR(512) CHARACTER SET utf8mb4 NOT NULL,
    NotifiedAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO Notification (NotificationName, Content) VALUES 
('Hot Trend', 'Check out the latest trending course!'),
('Discount Alert', 'A course in your wishlist is on sale!'),
('New Course', 'A new course has been added in your favorite category!'),
('Reminder', 'Don’t forget to complete your enrolled course!'),
('Special Offer', 'Limited-time discount on selected courses!');

-- Bảng NotificationDetail
CREATE TABLE NotificationDetail (
    NotificationID INT,
    UserID INT,
    Status ENUM('read', 'unread') NOT NULL DEFAULT 'unread',
    PRIMARY KEY (NotificationID, UserID),
    FOREIGN KEY (NotificationID) REFERENCES Notification(NotificationID)
);

INSERT INTO NotificationDetail (NotificationID, UserID, Status) VALUES 
(1, 1, 'read'), (2, 2, 'unread'), (3, 3, 'read'), (4, 4, 'unread'), (5, 5, 'read');

select * from MyCart;
select * from MyCartDetails;
select * from Payments;
select * from UserPayment;
select * from Invoices;
select * from InvoiceDetail;
select * from Notification;
select * from NotificationDetail;